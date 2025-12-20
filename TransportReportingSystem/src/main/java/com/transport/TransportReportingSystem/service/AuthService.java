package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.AuthRequest;
import com.transport.TransportReportingSystem.dto.AuthResponse;
import com.transport.TransportReportingSystem.dto.UserDTO;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.repository.UserRepository;
import com.transport.TransportReportingSystem.security.JwtService;
import com.transport.TransportReportingSystem.security.CustomUserDetailsService;
import com.transport.TransportReportingSystem.util.EmailTemplateUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    
    @Value("${application.security.2fa.code-expiration:300000}")
    private long twoFactorExpirationMs;

    public AuthResponse register(UserDTO request) {
        UserDTO createdUser = userService.createUser(request);
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(createdUser.getUserId())
                .name(createdUser.getName())
                .role(createdUser.getRole())
                .isTwoFactorEnabled(user.getIsTwoFactorEnabled())
                .build();
    }

    @Transactional
    public AuthResponse authenticate(@Valid AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        
        if (Boolean.TRUE.equals(user.getIsTwoFactorEnabled())) {
            // Generate new code
            String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
            tokenService.store2FAToken(user.getEmail(), code, (int) (twoFactorExpirationMs / 60000));
            
            String emailBody = EmailTemplateUtils.generate2FAEmail(user.getName(), code);
            emailService.sendEmail(user.getEmail(), "2FA Verification - KTRS", emailBody);
            
            return AuthResponse.builder()
                .isTwoFactorEnabled(true)
                .build();
        }

        var jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole().toString())
                .isTwoFactorEnabled(false)
                .build();
    }
    
    public AuthResponse verifyTwoFactor(String email, String code) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        
        if (!tokenService.validate2FAToken(email, code)) {
            throw new BadCredentialsException("Invalid or expired verification code");
        }
        
        var jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getUserId())
                .name(user.getName())
                .role(user.getRole().toString())
                .isTwoFactorEnabled(true)
                .build();
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = UUID.randomUUID().toString();
        tokenService.storePasswordResetToken(email, token, 30); // 30 minutes

        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        String emailBody = EmailTemplateUtils.generatePasswordResetEmail(user.getName(), resetLink);
        
        emailService.sendEmail(user.getEmail(), "Password Reset Request - KTRS", emailBody);
    }

    public void resetPassword(String token, String newPassword) {
        String email = tokenService.validatePasswordResetToken(token);
        if (email == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        tokenService.removePasswordResetToken(token);
    }
}

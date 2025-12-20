package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.AuthRequest;
import com.transport.TransportReportingSystem.dto.AuthResponse;
import com.transport.TransportReportingSystem.dto.UserDTO;
import com.transport.TransportReportingSystem.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Email @NotBlank @RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Password reset email sent (if user exists).");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @NotBlank @RequestParam String token, 
            @NotBlank @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully.");
    }
    
    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponse> verifyTwoFactor(@Valid @RequestBody com.transport.TransportReportingSystem.dto.VerificationRequest request) {
        return ResponseEntity.ok(authService.verifyTwoFactor(request.getEmail(), request.getCode()));
    }
}

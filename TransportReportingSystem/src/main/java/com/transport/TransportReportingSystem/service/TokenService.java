package com.transport.TransportReportingSystem.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TokenService {
    
    private final Map<String, TokenData> passwordResetTokens = new ConcurrentHashMap<>();
    private final Map<String, TokenData> twoFactorTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public TokenService() {
        // Clean expired tokens every 5 minutes
        scheduler.scheduleAtFixedRate(this::cleanExpiredTokens, 5, 5, TimeUnit.MINUTES);
    }
    
    public void storePasswordResetToken(String email, String token, int expirationMinutes) {
        passwordResetTokens.put(token, new TokenData(email, LocalDateTime.now().plusMinutes(expirationMinutes)));
    }
    
    public String validatePasswordResetToken(String token) {
        TokenData data = passwordResetTokens.get(token);
        if (data == null || data.isExpired()) {
            passwordResetTokens.remove(token);
            return null;
        }
        return data.getEmail();
    }
    
    public void removePasswordResetToken(String token) {
        passwordResetTokens.remove(token);
    }
    
    public void store2FAToken(String email, String code, int expirationMinutes) {
        String key = email + ":" + code;
        twoFactorTokens.put(key, new TokenData(email, LocalDateTime.now().plusMinutes(expirationMinutes)));
    }
    
    public boolean validate2FAToken(String email, String code) {
        String key = email + ":" + code;
        TokenData data = twoFactorTokens.get(key);
        if (data == null || data.isExpired()) {
            twoFactorTokens.remove(key);
            return false;
        }
        twoFactorTokens.remove(key); // One-time use
        return true;
    }
    
    private void cleanExpiredTokens() {
        passwordResetTokens.entrySet().removeIf(entry -> entry.getValue().isExpired());
        twoFactorTokens.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    private static class TokenData {
        private final String email;
        private final LocalDateTime expiresAt;
        
        public TokenData(String email, LocalDateTime expiresAt) {
            this.email = email;
            this.expiresAt = expiresAt;
        }
        
        public String getEmail() { return email; }
        public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }
    }
}
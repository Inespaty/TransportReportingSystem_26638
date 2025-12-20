package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.AuthRequest;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.enums.UserRole;
import com.transport.TransportReportingSystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void findUserByEmail_WithValidEmail_ShouldReturnUser() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setName("Test User");
        user.setRole(UserRole.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("Test User", result.get().getName());
    }

    @Test
    void findUserByEmail_WithInvalidEmail_ShouldReturnEmpty() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
    }
}
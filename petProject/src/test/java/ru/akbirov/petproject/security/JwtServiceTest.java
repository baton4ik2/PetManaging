package ru.akbirov.petproject.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "your-256-bit-secret-key-must-be-at-least-32-characters-long");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24 hours

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void testGenerateToken() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtService.validateToken(invalidToken, userDetails);
        });
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Given
        // Set expiration to 1ms to create an expired token
        ReflectionTestUtils.setField(jwtService, "expiration", 1L);
        String token = jwtService.generateToken(userDetails);

        // Wait a bit to ensure token is expired
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testExtractExpiration() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testExtractClaim() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String subject = jwtService.extractClaim(token, claims -> claims.getSubject());

        // Then
        assertEquals("testuser", subject);
    }
}


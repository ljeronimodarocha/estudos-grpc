package com.example.auth.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil;

    public JwtUtilTest() {
        String secretKey = "this-is-a-super-secret-key-for-testing-purposes-12345";
        jwtUtil = new JwtUtil(secretKey, 3600L, 86400L);
    }

    @Test
    void generateAccessToken_returnsValidToken() {
        // Arrange
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        // Act
        String token = jwtUtil.generateAccessToken(user);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void generateRefreshToken_returnsValidToken() {
        // Arrange
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();

        // Act
        String token = jwtUtil.generateRefreshToken(user);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        // Arrange
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();
        String token = jwtUtil.generateAccessToken(user);

        // Act
        boolean valid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(valid);
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        // Act
        boolean valid = jwtUtil.validateToken("invalid.token.here");

        // Assert
        assertFalse(valid);
    }

    @Test
    void validateToken_emptyString_returnsFalse() {
        // Act
        boolean valid = jwtUtil.validateToken("");

        // Assert
        assertFalse(valid);
    }

    @Test
    void getUsernameFromToken_returnsUsername() {
        // Arrange
        UserDetails user = User.builder()
                .username("john.doe")
                .password("password")
                .roles("USER")
                .build();
        String token = jwtUtil.generateAccessToken(user);

        // Act
        String username = jwtUtil.getUsernameFromToken(token);

        // Assert
        assertEquals("john.doe", username);
    }

    @Test
    void isTokenExpired_validToken_notExpired() {
        // Arrange
        UserDetails user = User.builder()
                .username("testuser")
                .password("password")
                .roles("USER")
                .build();
        String token = jwtUtil.generateAccessToken(user);

        // Act
        boolean expired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(expired);
    }
}

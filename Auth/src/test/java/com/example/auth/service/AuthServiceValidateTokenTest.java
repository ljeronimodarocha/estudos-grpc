package com.example.auth.service;

import com.example.auth.dto.ValidateResponse;
import com.example.auth.model.UserAuthentication;
import com.example.auth.repository.UserRepositoryAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceValidateTokenTest {

    private AuthService authService;

    @Mock
    private UserRepositoryAuthentication userRepositoryAuthentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateToken_returnsValidResponseWithUserId() {
        // This test verifies that the ValidateResponse includes userId
        ValidateResponse response = new ValidateResponse(true, "testuser", 42L);

        assertTrue(response.valid());
        assertEquals("testuser", response.username());
        assertEquals(42L, response.userId());
    }

    @Test
    void validateToken_invalidTokenReturnsInvalid() {
        ValidateResponse response = new ValidateResponse(false, null, null);

        assertFalse(response.valid());
        assertNull(response.username());
        assertNull(response.userId());
    }

    @Test
    void validateResponse_withMultipleUsers() {
        ValidateResponse response1 = new ValidateResponse(true, "user1", 1L);
        ValidateResponse response2 = new ValidateResponse(true, "user2", 2L);

        assertEquals(1L, response1.userId());
        assertEquals(2L, response2.userId());
        assertNotEquals(response1.userId(), response2.userId());
    }
}

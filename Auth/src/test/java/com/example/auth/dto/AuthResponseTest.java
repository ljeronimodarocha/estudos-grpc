package com.example.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {
    @Test
    void testFields() {
        AuthResponse resp = new AuthResponse("access", "refresh", "Bearer", 3600L);
        assertEquals("access", resp.accessToken());
        assertEquals("refresh", resp.refreshToken());
        assertEquals("Bearer", resp.tokenType());
        assertEquals(3600L, resp.expiresIn());
    }
}

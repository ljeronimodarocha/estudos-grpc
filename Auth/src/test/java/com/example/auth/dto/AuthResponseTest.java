package com.example.auth.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthResponseTest {
    @Test
    void testExpiresIn() {
        AuthResponse resp = new AuthResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 3600L);
        assertEquals(3600L, resp.expiresIn());
    }

    @Test
    void testExpiresInZero() {
        AuthResponse resp = new AuthResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString(),0L);
        assertEquals(0L, resp.expiresIn());
    }

    @Test
    void testExpiresInLargeValue() {
        AuthResponse resp = new AuthResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 86400L);
        assertEquals(86400L, resp.expiresIn());
    }
}

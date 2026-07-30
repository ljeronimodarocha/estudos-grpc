package com.example.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidateResponseTest {
    @Test
    void testFields() {
        ValidateResponse resp = new ValidateResponse(true, "user", 42L);
        assertEquals(true, resp.valid());
        assertEquals("user", resp.username());
        assertEquals(42L, resp.userId());
    }

    @Test
    void testInvalidToken() {
        ValidateResponse resp = new ValidateResponse(false, null, null);
        assertFalse(resp.valid());
        assertNull(resp.username());
        assertNull(resp.userId());
    }
}

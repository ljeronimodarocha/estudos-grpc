package com.example.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {
    @Test
    void testFields() {
        RegisterRequest req = new RegisterRequest("user", "pass", "email@example.com", "Display");
        assertEquals("user", req.username());
        assertEquals("pass", req.password());
        assertEquals("email@example.com", req.email());
        assertEquals("Display", req.displayName());
    }
}

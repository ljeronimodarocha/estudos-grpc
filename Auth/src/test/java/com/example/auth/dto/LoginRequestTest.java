package com.example.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {
    @Test
    void testFields() {
        LoginRequest req = new LoginRequest("user", "pass");
        assertEquals("user", req.username());
        assertEquals("pass", req.password());
    }
}

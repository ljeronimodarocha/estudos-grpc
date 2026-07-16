package com.example.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidateResponseTest {
    @Test
    void testFields() {
        ValidateResponse resp = new ValidateResponse(true, "user");
        assertEquals(true, resp.valid());
        assertEquals("user", resp.username());
    }
}

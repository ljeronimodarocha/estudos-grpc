package com.example.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.auth.dto.LogoutRequest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogoutRequestTest {
    @Test
    void testFields() {
        LogoutRequest req = new LogoutRequest("myToken");
        assertEquals("myToken", req.refreshToken());
    }
}

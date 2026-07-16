package com.example.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.auth.dto.RefreshRequest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RefreshRequestTest {
    @Test
    void testFields() {
        RefreshRequest req = new RefreshRequest("myToken");
        assertEquals("myToken", req.refreshToken());
    }
}

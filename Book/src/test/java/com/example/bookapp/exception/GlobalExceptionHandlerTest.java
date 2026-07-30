package com.example.bookapp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlePermissionDenied_returnsForbidden() {
        PermissionDeniedException ex = new PermissionDeniedException("You do not have permission to update this book");
        ResponseEntity<?> response = handler.handlePermissionDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        com.example.bookapp.dto.ErrorResponse error = (com.example.bookapp.dto.ErrorResponse) response.getBody();
        assertNotNull(error);
        assertTrue(error.message().contains("permission"));
    }

    @Test
    void handlePermissionDenied_messageContainsDetail() {
        PermissionDeniedException ex = new PermissionDeniedException("Access denied for user");
        ResponseEntity<?> response = handler.handlePermissionDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        com.example.bookapp.dto.ErrorResponse error = (com.example.bookapp.dto.ErrorResponse) response.getBody();
        assertNotNull(error);
    }
}

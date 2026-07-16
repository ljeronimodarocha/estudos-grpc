package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.RefreshRequest;
import com.example.auth.dto.LogoutRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.ValidateResponse;
import com.example.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @Test
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest("user", "pass");
        AuthResponse resp = new AuthResponse("ac", "rf", "Bearer", 3600L);
        when(authService.login(req)).thenReturn(resp);
        ResponseEntity<AuthResponse> result = controller.login(req);
        assertNotNull(result);
        assertEquals("ac", result.getBody().accessToken());
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("newuser", "pass123", "email@example.com", "New User");
        AuthResponse resp = new AuthResponse("token123", "refresh123", "Bearer", 3600L);
        when(authService.register(req)).thenReturn(resp);
        ResponseEntity<AuthResponse> result = controller.register(req);
        assertNotNull(result);
        assertEquals("token123", result.getBody().accessToken());
        assertEquals("Bearer", result.getBody().tokenType());
    }

    @Test
    void testRefreshSuccess() {
        RefreshRequest req = new RefreshRequest("oldRefreshToken");
        AuthResponse resp = new AuthResponse("newToken", "newRefreshToken", "Bearer", 3600L);
        when(authService.refresh(req)).thenReturn(resp);
        ResponseEntity<AuthResponse> result = controller.refresh(req);
        assertNotNull(result);
        assertEquals("newToken", result.getBody().accessToken());
    }

    @Test
    void testLogoutSuccess() {
        LogoutRequest req = new LogoutRequest("someRefreshToken");
        doNothing().when(authService).logout(req);
        ResponseEntity<Void> result = controller.logout(req);
        assertNotNull(result);
    }

    @Test
    void testValidateTokenValid() {
        String token = "validToken123";
        ValidateResponse resp = new ValidateResponse(true, "username");
        when(authService.validateToken(token)).thenReturn(resp);
        ResponseEntity<ValidateResponse> result = controller.validate(token);
        assertNotNull(result);
        assertEquals(true, result.getBody().valid());
        assertEquals("username", result.getBody().username());
    }

    @Test
    void testValidateTokenInvalid() {
        String token = "invalidToken";
        ValidateResponse resp = new ValidateResponse(false, null);
        when(authService.validateToken(token)).thenReturn(resp);
        ResponseEntity<ValidateResponse> result = controller.validate(token);
        assertNotNull(result);
        assertEquals(false, result.getBody().valid());
    }
}

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
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

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
        AuthResponse resp = new AuthResponse("accessToken", "refreshToken", 3600L);
        when(authService.login(req)).thenReturn(resp);
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<?> result = controller.login(req, response);
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(3600L, ((Map<?,?>) result.getBody()).get("expiresIn"));
        assertEquals("accessToken", response.getCookie("access_token").getValue());
        assertEquals("refreshToken", response.getCookie("refresh_token").getValue());
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest("newuser", "pass123", "email@example.com", "New User");
        AuthResponse resp = new AuthResponse("accessToken", "refreshToken", 3600L);
        when(authService.register(req)).thenReturn(resp);
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<?> result = controller.register(req, response);
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(3600L, ((Map<?,?>) result.getBody()).get("expiresIn"));
        assertEquals("accessToken", response.getCookie("access_token").getValue());
        assertEquals("refreshToken", response.getCookie("refresh_token").getValue());
    }

    @Test
    void testRefreshSuccess() {
        RefreshRequest req = new RefreshRequest("oldRefreshToken");
        AuthResponse resp = new AuthResponse("newAccessToken", "newRefreshToken", 3600L);
        when(authService.refresh(req.refreshToken())).thenReturn(resp);
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<?> result = controller.refresh(req, null, response);
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertEquals(3600L, ((Map<?,?>) result.getBody()).get("expiresIn"));
        assertEquals("newAccessToken", response.getCookie("access_token").getValue());
        assertEquals("newRefreshToken", response.getCookie("refresh_token").getValue());
    }

    @Test
    void testLogoutSuccess() {
        LogoutRequest req = new LogoutRequest("someRefreshToken");
        doNothing().when(authService).logout("someRefreshToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseEntity<?> result = controller.logout(req, null, response);
        assertNotNull(result);
    }

    @Test
    void testValidateTokenValid() {
        String token = "validToken123";
        ValidateResponse resp = new ValidateResponse(true, "username", 1L);
        when(authService.validateToken(token)).thenReturn(resp);
        ResponseEntity<ValidateResponse> result = controller.validate(token);
        assertNotNull(result);
        assertEquals(true, result.getBody().valid());
        assertEquals("username", result.getBody().username());
        assertEquals(1L, result.getBody().userId());
    }

    @Test
    void testValidateTokenInvalid() {
        String token = "invalidToken";
        ValidateResponse resp = new ValidateResponse(false, null, null);
        when(authService.validateToken(token)).thenReturn(resp);
        ResponseEntity<ValidateResponse> result = controller.validate(token);
        assertNotNull(result);
        assertEquals(false, result.getBody().valid());
    }
}

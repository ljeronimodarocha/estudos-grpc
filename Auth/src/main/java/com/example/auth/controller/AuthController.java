package com.example.auth.controller;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LogoutRequest;
import com.example.auth.dto.RefreshRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.ValidateResponse;
import com.example.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setAccessTokenCookie(response, authResponse.expiresIn());
        setRefreshTokenCookie(response, (long) 86400);
        return ResponseEntity.ok(Map.of("expiresIn", authResponse.expiresIn()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setAccessTokenCookie(response, authResponse.expiresIn());
        setRefreshTokenCookie(response, (long) 86400);
        return ResponseEntity.ok(Map.of("expiresIn", authResponse.expiresIn()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.refresh(request);
        setAccessTokenCookie(response, authResponse.expiresIn());
        setRefreshTokenCookie(response, (long) 86400);
        return ResponseEntity.ok(Map.of("expiresIn", authResponse.expiresIn()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request, HttpServletResponse response) {
        authService.logout(request);
        removeAccessTokenCookie(response);
        removeRefreshTokenCookie(response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    private void setAccessTokenCookie(HttpServletResponse response, long maxAgeSeconds) {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAgeSeconds);
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, long maxAgeSeconds) {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAgeSeconds);
        response.addCookie(cookie);
    }

    private void removeAccessTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

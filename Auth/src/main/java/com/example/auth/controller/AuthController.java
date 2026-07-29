package com.example.auth.controller;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.LogoutRequest;
import com.example.auth.dto.RefreshRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.ValidateResponse;
import com.example.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setAccessTokenCookie(response, authResponse.accessToken());
        setRefreshTokenCookie(response, authResponse.refreshToken());
        return ResponseEntity.ok(Map.of("expiresIn", authResponse.expiresIn()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setAccessTokenCookie(response, authResponse.accessToken());
        setRefreshTokenCookie(response, authResponse.refreshToken());
        return ResponseEntity.ok(Map.of("expiresIn", authResponse.expiresIn()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody(required = false) RefreshRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        String refreshToken = request != null ? request.refreshToken() : extractRefreshTokenFromCookie(httpRequest);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "refresh token is required"));
        }
        AuthResponse authResponse = authService.refresh(refreshToken);
        setAccessTokenCookie(response, authResponse.accessToken());
        setRefreshTokenCookie(response, authResponse.refreshToken());
        return ResponseEntity.ok(Map.of("expiresIn", authResponse.expiresIn()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) LogoutRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        String refreshToken = request != null ? request.refreshToken() : extractRefreshTokenFromCookie(httpRequest);
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "refresh token is required"));
        }
        authService.logout(refreshToken);
        removeAccessTokenCookie(response);
        removeRefreshTokenCookie(response);
        return ResponseEntity.ok().build();
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    log.debug("Found refresh_token in cookie");
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
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

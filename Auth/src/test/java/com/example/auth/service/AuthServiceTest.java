package com.example.auth.service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.RefreshRequest;
import com.example.auth.dto.LogoutRequest;
import com.example.auth.model.UserAuthentication;
import com.example.auth.model.Token;
import com.example.auth.util.JwtUtil;
import com.example.grpc.user.UserServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserServiceAuth userServiceAuth;

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserServiceGrpc.UserServiceBlockingStub userGrpcStub;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private RefreshRequest refreshRequest;
    private LogoutRequest logoutRequest;
    private UserAuthentication userAuthentication;
    private UserDetails userDetails;
    private AuthResponse authResponse;
    private Token mockToken;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("testuser", "password123");
        registerRequest = new RegisterRequest("newuser", "newpassword", "email@test.com", "DisplayName");
        refreshRequest = new RefreshRequest("validRefreshToken");
        logoutRequest = new LogoutRequest("validRefreshToken");

        userAuthentication = mock(UserAuthentication.class);
        

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("encodedPassword")
                .disabled(false)
                .build();

        authResponse = new AuthResponse("accessToken", "refreshToken", "Bearer", 3600L);
        mockToken = mock(Token.class);
    }

    @Test
    void login_success() {
        when(userGrpcStub.getUserByUsername(any())).thenReturn(
            com.example.grpc.user.UserResponse.newBuilder()
                .setId(1)
                .setName(loginRequest.username())
                .setEmail("test@test.com")
                .setDisplayName("Test User")
                .build()
        );
        when(userServiceAuth.findByUsername(anyString())).thenReturn(userAuthentication);
        when(userServiceAuth.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        when(tokenService.saveToken(any(), any(), any(), anyLong())).thenReturn(mock(Token.class));

        AuthResponse result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        assertEquals("Bearer", result.tokenType());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_usernameNotFound() {
        when(userGrpcStub.getUserByUsername(any())).thenReturn(
            com.example.grpc.user.UserResponse.newBuilder()
                .setId(1)
                .setName(loginRequest.username())
                .setEmail("test@test.com")
                .setDisplayName("Test User")
                .build()
        );
        when(userServiceAuth.findByUsername(anyString())).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_authenticationFailure() {
        when(userGrpcStub.getUserByUsername(any())).thenReturn(
            com.example.grpc.user.UserResponse.newBuilder()
                .setId(1)
                .setName(loginRequest.username())
                .setEmail("test@test.com")
                .setDisplayName("Test User")
                .build()
        );
        doThrow(new RuntimeException("Invalid credentials"))
            .when(authenticationManager)
            .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void register_success() {
        when(userGrpcStub.register(any())).thenReturn(
            com.example.grpc.user.UserResponse.newBuilder()
                .setId(1)
                .setName(registerRequest.username())
                .setEmail(registerRequest.email())
                .setDisplayName(registerRequest.displayName())
                .build()
        );
        when(userServiceAuth.registerUserAuthentication(any(), eq(1L))).thenReturn(userAuthentication);
        when(userServiceAuth.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(any(UserDetails.class))).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn("refreshToken");
        when(tokenService.saveToken(any(), any(), any(), anyLong())).thenReturn(mock(Token.class));

        AuthResponse result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
        verify(userServiceAuth).registerUserAuthentication(eq(registerRequest), eq(1L));
    }

    @Test
    void register_grpcFailure() {
        when(userGrpcStub.register(any())).thenThrow(new RuntimeException("gRPC error"));

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
    }

    @Test
    void refresh_success() {
        when(tokenService.isTokenValid(anyString())).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(userServiceAuth.findByUsername(anyString())).thenReturn(userAuthentication);
        when(userServiceAuth.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(any(UserDetails.class))).thenReturn("newAccessToken");
        when(jwtUtil.generateRefreshToken(any(UserDetails.class))).thenReturn("newRefreshToken");
        doNothing().when(tokenService).revokeAllUserTokens(any(UserAuthentication.class));
        when(tokenService.saveToken(any(), any(), any(), anyLong())).thenReturn(mock(Token.class));

        AuthResponse result = authService.refresh(refreshRequest);

        assertNotNull(result);
        assertEquals("newAccessToken", result.accessToken());
        assertEquals("newRefreshToken", result.refreshToken());
        verify(tokenService).revokeAllUserTokens(any(UserAuthentication.class));
    }

    @Test
    void refresh_invalidToken() {
        when(tokenService.isTokenValid(anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.refresh(refreshRequest));
    }

    @Test
    void logout_success() {
        when(tokenService.findByToken(anyString())).thenReturn(Optional.of(mockToken));

        authService.logout(logoutRequest);

        verify(tokenService).findByToken(anyString());
        verify(tokenService).revokeToken(any(Token.class));
    }

    @Test
    void logout_noTokenFound() {
        when(tokenService.findByToken(anyString())).thenReturn(Optional.empty());

        authService.logout(logoutRequest);

        verify(tokenService).findByToken(anyString());
        verify(tokenService, never()).revokeToken(any());
    }

    @Test
    void validateToken_validToken() {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("testuser");

        var result = authService.validateToken("validToken");

        assertNotNull(result);
        assertTrue(result.valid());
        assertEquals("testuser", result.username());
    }

    @Test
    void validateToken_invalidToken() {
        when(jwtUtil.validateToken(anyString())).thenReturn(false);

        var result = authService.validateToken("invalidToken");

        assertNotNull(result);
        assertFalse(result.valid());
        assertNull(result.username());
    }
}

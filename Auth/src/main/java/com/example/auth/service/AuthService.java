package com.example.auth.service;

import com.example.auth.dto.*;
import com.example.auth.model.Token;
import com.example.auth.model.UserAuthentication;
import com.example.auth.util.JwtUtil;
import com.example.grpc.user.*;
import com.example.grpc.user.UserServiceGrpc;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class AuthService {

    private AuthenticationManager authenticationManager;
    private UserServiceAuth userServiceAuth;
    private TokenService tokenService;
    private JwtUtil jwtUtil;
    private long accessTokenValiditySeconds;
    private long refreshTokenValiditySeconds;
    private UserServiceGrpc.UserServiceBlockingStub userGrpcStub;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserServiceAuth userServiceAuth,
            TokenService tokenService,
            JwtUtil jwtUtil,
            @Value("${jwt.token-validity-seconds}") long accessTokenValiditySeconds,
            @Value("${jwt.refresh-token-validity-seconds}") long refreshTokenValiditySeconds,
            UserServiceGrpc.UserServiceBlockingStub userGrpcStub
    ) {
        this.authenticationManager = authenticationManager;
        this.userServiceAuth = userServiceAuth;
        this.tokenService = tokenService;
        this.jwtUtil = jwtUtil;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        this.userGrpcStub = userGrpcStub;
    }

    public AuthResponse login(LoginRequest request) {
        // Buscar usuário no User Service via gRPC
        com.example.grpc.user.GetUserByUsernameRequest getUserRequest =
            com.example.grpc.user.GetUserByUsernameRequest.newBuilder()
                .setUsername(request.username())
                .build();
        
        com.example.grpc.user.UserResponse userResponse = userGrpcStub.getUserByUsername(getUserRequest);

        // Autenticar via Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Buscar UserAuthentication para gerar tokens
        UserAuthentication userAuthentication = userServiceAuth.findByUsername(request.username());

        String accessToken = jwtUtil.generateAccessToken(userServiceAuth.loadUserByUsername(userAuthentication.getUsername()));
        String refreshToken = jwtUtil.generateRefreshToken(userServiceAuth.loadUserByUsername(userAuthentication.getUsername()));

        tokenService.saveToken(userAuthentication, refreshToken, Token.TokenType.REFRESH, refreshTokenValiditySeconds);

        return new AuthResponse(accessToken, refreshToken, "Bearer", accessTokenValiditySeconds);
    }

    public AuthResponse register(com.example.auth.dto.RegisterRequest request) {
        com.example.grpc.user.RegisterRequest userRequest = com.example.grpc.user.RegisterRequest.newBuilder()
                .setUsername(request.username())
                .setEmail(request.email())
                .setDisplayName(request.displayName())
                .build();

        UserResponse registered = userGrpcStub.register(userRequest);

        UserAuthentication userAuthentication = userServiceAuth.registerUserAuthentication(request, (long) registered.getId());
        UserDetails userDetails = userServiceAuth.loadUserByUsername(userAuthentication.getUsername());

        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        tokenService.saveToken(userAuthentication, refreshToken, Token.TokenType.REFRESH, refreshTokenValiditySeconds);

        return new AuthResponse(accessToken, refreshToken, "Bearer", accessTokenValiditySeconds);
    }

    public AuthResponse refresh(RefreshRequest request) {
        String refreshTokenStr = request.refreshToken();

        if (!tokenService.isTokenValid(refreshTokenStr)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String username = jwtUtil.getUsernameFromToken(refreshTokenStr);
        UserAuthentication userAuthentication = userServiceAuth.findByUsername(username);

        tokenService.revokeAllUserTokens(userAuthentication);

        UserDetails userDetails = userServiceAuth.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        tokenService.saveToken(userAuthentication, newRefreshToken, Token.TokenType.REFRESH, refreshTokenValiditySeconds);

        return new AuthResponse(newAccessToken, newRefreshToken, "Bearer", accessTokenValiditySeconds);
    }

    public void logout(LogoutRequest request) {
        tokenService.findByToken(request.refreshToken())
                .ifPresent(tokenService::revokeToken);
    }

    public ValidateResponse validateToken(String token) {
        boolean valid = jwtUtil.validateToken(token);
        String username = null;
        if (valid) {
            username = jwtUtil.getUsernameFromToken(token);
        }
        return new ValidateResponse(valid, username);
    }
}

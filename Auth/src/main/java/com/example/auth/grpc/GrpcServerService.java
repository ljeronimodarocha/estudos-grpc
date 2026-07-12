package com.example.auth.grpc;

import com.example.auth.service.AuthService;
import com.example.auth.util.JwtUtil;
import com.example.grpc.auth.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcServerService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Override
    public void login(LoginRequest request, StreamObserver<AuthResponse> responseObserver) {
        try {
            com.example.auth.dto.LoginRequest loginRequest = new com.example.auth.dto.LoginRequest(
                    request.getUsername(),
                    request.getPassword()
            );
            com.example.auth.dto.AuthResponse authResponse = authService.login(loginRequest);
            AuthResponse response = AuthResponse.newBuilder()
                    .setAccessToken(authResponse.accessToken())
                    .setRefreshToken(authResponse.refreshToken())
                    .setTokenType(authResponse.tokenType())
                    .setExpiresIn(authResponse.expiresIn())
                    .setAccessToken(authResponse.accessToken())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Erro interno no servidor: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<AuthResponse> responseObserver) {
        super.register(request, responseObserver);
    }

    @Override
    public void refresh(RefreshRequest request, StreamObserver<AuthResponse> responseObserver) {
        super.refresh(request, responseObserver);
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<Empty> responseObserver) {
        super.logout(request, responseObserver);
    }

    @Override
    public void validate(ValidateTokenRequest request, StreamObserver<ValidateResponse> responseObserver) {
        try {
            String token = request.getToken();
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                ValidateResponse response = ValidateResponse.newBuilder()
                    .setValid(true)
                    .setUsername(username)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                Status status = Status.UNAUTHENTICATED
                    .withDescription("Token inválido ou expirado");
                responseObserver.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Erro ao validar token: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }
}

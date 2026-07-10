package com.example.auth.grpc;

import com.example.auth.service.AuthService;
import com.example.grpc.auth.*;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@AllArgsConstructor
public class GrpcServerService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;

    @Override
    public void login(LoginRequest request, StreamObserver<AuthResponse> responseObserver) {
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
        super.validate(request, responseObserver);
    }
}

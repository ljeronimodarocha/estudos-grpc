package com.example.user.grpc;

import com.example.grpc.user.GetUserByIdRequest;
import com.example.grpc.user.RegisterRequest;
import com.example.grpc.user.UserResponse;
import com.example.grpc.user.UserServiceGrpc;
import com.example.user.model.User;
import com.example.user.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class GrpcServerService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Autowired
    public GrpcServerService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            com.example.user.dto.RegisterRequest registerRequest =
                    new com.example.user.dto.RegisterRequest(request.getUsername(),
                            request.getEmail(),
                            request.getDisplayName());
            User user = userService.registerUser(registerRequest);
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId().intValue())
                    .setName(user.getName())
                    .setDisplayName(user.getDisplayName())
                    .setEmail(user.getEmail())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Erro interno no servidor: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            User user = userService.findById((long) request.getId());
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId().intValue())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setDisplayName(user.getDisplayName())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Erro ao buscar usuário por ID: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getUserByUsername(com.example.grpc.user.GetUserByUsernameRequest request,
                                  StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
        try {
            User user = userService.findByName(request.getUsername());
            com.example.grpc.user.UserResponse response = com.example.grpc.user.UserResponse.newBuilder()
                    .setId(user.getId().intValue())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setDisplayName(user.getDisplayName())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Erro ao buscar usuário por username: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getUserByIdentity(com.example.grpc.user.GetUserByIdentityRequest request,
                                  StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
        try {
            User user = userService.findByIdentity(request.getUserId());
            com.example.grpc.user.UserResponse response = com.example.grpc.user.UserResponse.newBuilder()
                    .setId(user.getId().intValue())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setDisplayName(user.getDisplayName())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Erro ao buscar usuário por IDENTITY: " + e.getMessage());
            responseObserver.onError(status.asRuntimeException());
        }
    }

}

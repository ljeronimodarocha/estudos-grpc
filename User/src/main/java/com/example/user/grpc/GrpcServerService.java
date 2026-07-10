package com.example.user.grpc;

import com.example.grpc.user.GetUserByIdRequest;
import com.example.grpc.user.RegisterRequest;
import com.example.grpc.user.UserResponse;
import com.example.grpc.user.UserServiceGrpc;
import com.example.user.model.User;
import com.example.user.service.UserService;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@AllArgsConstructor
public class GrpcServerService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public void register(RegisterRequest request, StreamObserver<UserResponse> responseObserver) {
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
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        User user = userService.findById((long) request.getId());
        UserResponse response = UserResponse.newBuilder()
                .setId(user.getId().intValue())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setDisplayName(user.getDisplayName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


}

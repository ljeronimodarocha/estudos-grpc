package com.example.user.grpc;

import com.example.grpc.user.*;
import com.example.user.config.GrpcClientFactory;
import com.example.user.filter.JwtValidationFilter;
import com.example.user.model.User;
import com.example.user.service.UserService;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcServerServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private GrpcClientFactory grpcClientFactory;

    @Mock
    private JwtValidationFilter jwtValidationFilter;

    @InjectMocks
    private GrpcServerService grpcServerService;

    @BeforeEach
    void setUp() {
        lenient().when(grpcClientFactory.getAuthServiceBlockingStub()).thenReturn(
                mock(com.example.grpc.auth.AuthServiceGrpc.AuthServiceBlockingStub.class));
    }

    @Test
    void register_success() {
        RegisterRequest grpcRequest = RegisterRequest.newBuilder()
                .setUsername("grpcuser")
                .setEmail("grpc@example.com")
                .setDisplayName("gRPC User")
                .build();

        User savedUser = User.builder()
                .name("grpcuser")
                .email("grpc@example.com")
                .displayName("gRPC User")
                .enabled(true)
                .id(1L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.registerUser(any(com.example.user.dto.RegisterRequest.class))).thenReturn(savedUser);

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.register(grpcRequest, responseObserver);

        verify(responseObserver, times(1)).onNext(any(UserResponse.class));
        verify(responseObserver, times(1)).onCompleted();
        verify(userService, times(1)).registerUser(any(com.example.user.dto.RegisterRequest.class));
    }

    @Test
    void register_userNotFound_sendsError() {
        RegisterRequest grpcRequest = RegisterRequest.newBuilder()
                .setUsername("nonexistent")
                .setEmail("no@example.com")
                .setDisplayName("No One")
                .build();

        when(userService.registerUser(any(com.example.user.dto.RegisterRequest.class)))
                .thenThrow(new RuntimeException("User not found"));

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.register(grpcRequest, responseObserver);

        verify(responseObserver, never()).onNext(any(UserResponse.class));
        verify(responseObserver, times(1)).onError(any(StatusRuntimeException.class));
    }

    @Test
    void getUserById_success() {
        User user = User.builder()
                .name("user1")
                .email("user1@example.com")
                .displayName("User 1")
                .enabled(true)
                .id(1L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.findById(1L)).thenReturn(user);

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.getUserById(GetUserByIdRequest.newBuilder().setId(1).build(), responseObserver);

        UserResponse capturedResponse = captureResponse(responseObserver);
        assertThat(capturedResponse.getName()).isEqualTo("user1");
        assertThat(capturedResponse.getEmail()).isEqualTo("user1@example.com");
        assertThat(capturedResponse.getId()).isEqualTo(1);
        assertThat(capturedResponse.getDisplayName()).isEqualTo("User 1");
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void getUserById_notFound_sendsError() {
        when(userService.findById(999L)).thenThrow(new RuntimeException("User not found: 999"));

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.getUserById(GetUserByIdRequest.newBuilder().setId(999).build(), responseObserver);

        verify(responseObserver, never()).onNext(any(UserResponse.class));
        verify(responseObserver, times(1)).onError(any(StatusRuntimeException.class));
    }

    @Test
    void getUserByUsername_success() {
        User user = User.builder()
                .name("usernameuser")
                .email("username@example.com")
                .displayName("Username User")
                .enabled(true)
                .id(5L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.findByName("usernameuser")).thenReturn(user);

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.getUserByUsername(
                GetUserByUsernameRequest.newBuilder().setUsername("usernameuser").build(),
                responseObserver);

        UserResponse capturedResponse = captureResponse(responseObserver);
        assertThat(capturedResponse.getName()).isEqualTo("usernameuser");
        assertThat(capturedResponse.getEmail()).isEqualTo("username@example.com");
        assertThat(capturedResponse.getId()).isEqualTo(5);
        verify(userService, times(1)).findByName("usernameuser");
    }

    @Test
    void getUserByUsername_notFound_sendsError() {
        when(userService.findByName("nonexistent")).thenThrow(new RuntimeException("User not found"));

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.getUserByUsername(
                GetUserByUsernameRequest.newBuilder().setUsername("nonexistent").build(),
                responseObserver);

        verify(responseObserver, never()).onNext(any(UserResponse.class));
        verify(responseObserver, times(1)).onError(any(StatusRuntimeException.class));
    }

    @Test
    void getUserByIdentity_success() {
        User user = User.builder()
                .name("identityuser")
                .email("identity@example.com")
                .displayName("Identity User")
                .enabled(true)
                .id(10L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.findByIdentity("10")).thenReturn(user);

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.getUserByIdentity(
                GetUserByIdentityRequest.newBuilder().setUserId("10").build(),
                responseObserver);

        UserResponse capturedResponse = captureResponse(responseObserver);
        assertThat(capturedResponse.getName()).isEqualTo("identityuser");
        assertThat(capturedResponse.getEmail()).isEqualTo("identity@example.com");
        assertThat(capturedResponse.getId()).isEqualTo(10);
        verify(userService, times(1)).findByIdentity("10");
    }

    @Test
    void getUserByIdentity_invalidId_throwsException() {
        when(userService.findByIdentity("invalid")).thenThrow(new RuntimeException("Invalid user ID"));

        StreamObserver<UserResponse> responseObserver = mock(StreamObserver.class);
        grpcServerService.getUserByIdentity(
                GetUserByIdentityRequest.newBuilder().setUserId("invalid").build(),
                responseObserver);

        verify(responseObserver, never()).onNext(any(UserResponse.class));
        verify(responseObserver, times(1)).onError(any(StatusRuntimeException.class));
    }

    private UserResponse captureResponse(StreamObserver<UserResponse> responseObserver) {
        ArgumentCaptor<UserResponse> captor = ArgumentCaptor.forClass(UserResponse.class);
        verify(responseObserver, times(1)).onNext(captor.capture());
        return captor.getValue();
    }
}

package com.example.auth.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class GrpcExceptionHandlerTest {

    @Test
    public void shouldMapBadCredentialsExceptionToUnauthenticated() {
        BadCredentialsException ex = new BadCredentialsException("Invalid username or password");
        StatusRuntimeException result = GrpcExceptionHandler.handleException(ex);

        assertEquals(Status.UNAUTHENTICATED.getCode(), result.getStatus().getCode());
        assertEquals("Invalid username or password", result.getStatus().getDescription());
    }

    @Test
    public void shouldMapAuthenticationExceptionToUnauthenticated() {
        AuthenticationException ex = new AuthenticationException("Authentication failed") {};
        StatusRuntimeException result = GrpcExceptionHandler.handleException(ex);

        assertEquals(Status.UNAUTHENTICATED.getCode(), result.getStatus().getCode());
        assertEquals("Authentication failed", result.getStatus().getDescription());
    }

    @Test
    public void shouldMapAccessDeniedExceptionToPermissionDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        StatusRuntimeException result = GrpcExceptionHandler.handleException(ex);

        assertEquals(Status.PERMISSION_DENIED.getCode(), result.getStatus().getCode());
        assertEquals("Access denied", result.getStatus().getDescription());
    }

    @Test
    public void shouldMapUsernameNotFoundExceptionToNotFound() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found: test") {};
        StatusRuntimeException result = GrpcExceptionHandler.handleException(ex);

        assertEquals(Status.NOT_FOUND.getCode(), result.getStatus().getCode());
        assertEquals("User not found: test", result.getStatus().getDescription());
    }

    @Test
    public void shouldMapIllegalArgumentExceptionToInvalidArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");
        StatusRuntimeException result = GrpcExceptionHandler.handleException(ex);

        assertEquals(Status.INVALID_ARGUMENT.getCode(), result.getStatus().getCode());
        assertEquals("Invalid input", result.getStatus().getDescription());
    }

    @Test
    public void shouldMapRuntimeExceptionToInternal() {
        RuntimeException ex = new RuntimeException("Internal error");
        StatusRuntimeException result = GrpcExceptionHandler.handleException(ex);

        assertEquals(Status.INTERNAL.getCode(), result.getStatus().getCode());
        assertEquals("Internal error", result.getStatus().getDescription());
    }

    @Test
    public void shouldMapGenericExceptionToInternal() {
        Exception ex = new Exception("Unexpected error");
        StatusRuntimeException result = GrpcExceptionHandler.handleException(ex);

        assertEquals(Status.INTERNAL.getCode(), result.getStatus().getCode());
        assertEquals("An unexpected error occurred", result.getStatus().getDescription());
    }
}

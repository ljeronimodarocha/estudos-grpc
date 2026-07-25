package com.example.auth.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class GrpcClientExceptionHandlerTest {

    @Test
    public void shouldConvertUnauthenticatedToBadCredentials() {
        StatusRuntimeException e = new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid credentials"));
        RuntimeException result = GrpcClientExceptionHandler.wrapRuntimeException(e);

        assertTrue(result.getCause() instanceof BadCredentialsException);
        assertEquals("Invalid credentials", result.getCause().getMessage());
    }

    @Test
    public void shouldConvertNotFoundToUsernameNotFoundException() {
        StatusRuntimeException e = new StatusRuntimeException(Status.NOT_FOUND.withDescription("User not found"));
        RuntimeException result = GrpcClientExceptionHandler.wrapRuntimeException(e);

        assertTrue(result.getCause() instanceof UsernameNotFoundException);
        assertEquals("User not found", result.getCause().getMessage());
    }

    @Test
    public void shouldConvertPermissionDeniedToAccessDenied() {
        StatusRuntimeException e = new StatusRuntimeException(Status.PERMISSION_DENIED.withDescription("Access denied"));
        RuntimeException result = GrpcClientExceptionHandler.wrapRuntimeException(e);

        assertTrue(result.getCause() instanceof AccessDeniedException);
        assertEquals("Access denied", result.getCause().getMessage());
    }

    @Test
    public void shouldConvertInvalidArgumentToIllegalArgumentException() {
        StatusRuntimeException e = new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Invalid input"));
        RuntimeException result = GrpcClientExceptionHandler.wrapRuntimeException(e);

        assertTrue(result.getCause() instanceof IllegalArgumentException);
        assertEquals("Invalid input", result.getCause().getMessage());
    }

    @Test
    public void shouldConvertInternalToRuntimeException() {
        StatusRuntimeException e = new StatusRuntimeException(Status.INTERNAL.withDescription("Internal error"));
        RuntimeException result = GrpcClientExceptionHandler.wrapRuntimeException(e);

        assertTrue(result.getCause() instanceof RuntimeException);
        assertEquals("Internal error", result.getCause().getMessage());
    }
}

package com.example.auth.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GrpcExceptionHandler {

    public static StatusRuntimeException handleException(Throwable ex) {
        if (ex instanceof UsernameNotFoundException e) {
            return new StatusRuntimeException(Status.NOT_FOUND.withDescription(e.getMessage()));
        }
        if (ex instanceof BadCredentialsException e) {
            return new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid username or password"));
        }
        if (ex instanceof AuthenticationException e) {
            return new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription(e.getMessage()));
        }
        if (ex instanceof AccessDeniedException e) {
            return new StatusRuntimeException(Status.PERMISSION_DENIED.withDescription("Access denied"));
        }
        if (ex instanceof IllegalArgumentException e) {
            return new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()));
        }
        if(ex instanceof StatusRuntimeException e){
            return e;
        }
        if (ex instanceof RuntimeException e) {
            return new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMessage()));
        }
        return new StatusRuntimeException(Status.INTERNAL.withDescription("An unexpected error occurred"));
    }
}

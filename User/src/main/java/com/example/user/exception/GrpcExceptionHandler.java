package com.example.user.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GrpcExceptionHandler {

    public static StatusRuntimeException handleException(Throwable ex) {
        if (ex instanceof UsernameNotFoundException e) {
            return new StatusRuntimeException(Status.NOT_FOUND.withDescription(e.getMessage()));
        }
        if (ex instanceof IllegalArgumentException e) {
            return new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(e.getMessage()));
        }
        if (ex instanceof RuntimeException e) {
            return new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMessage()));
        }
        return new StatusRuntimeException(Status.INTERNAL.withDescription("An unexpected error occurred"));
    }
}

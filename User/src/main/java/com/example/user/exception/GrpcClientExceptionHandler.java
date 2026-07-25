package com.example.user.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class GrpcClientExceptionHandler {

    public static Throwable handleStatus(Status status) {
        switch (status.getCode()) {
            case NOT_FOUND:
                return new UsernameNotFoundException(status.getDescription());
            case INVALID_ARGUMENT:
                return new IllegalArgumentException(status.getDescription());
            case INTERNAL:
            default:
                return new RuntimeException(status.getDescription() != null ? status.getDescription() : "Internal gRPC error");
        }
    }

    public static RuntimeException wrapRuntimeException(StatusRuntimeException e) {
        Status status = e.getStatus();
        Throwable exception = handleStatus(status);
        return new RuntimeException(exception);
    }
}

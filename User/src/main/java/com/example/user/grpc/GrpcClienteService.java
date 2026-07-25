package com.example.user.grpc;

import com.example.user.exception.GrpcClientExceptionHandler;
import io.grpc.StatusRuntimeException;

public class GrpcClienteService {

    public static RuntimeException handleClientError(StatusRuntimeException e) {
        return GrpcClientExceptionHandler.wrapRuntimeException(e);
    }
}

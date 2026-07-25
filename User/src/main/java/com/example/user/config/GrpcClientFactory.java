package com.example.user.config;

import com.example.grpc.auth.AuthServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientFactory {

    private static final Logger log = LoggerFactory.getLogger(GrpcClientFactory.class);

    @Value("${grpc.client.address:localhost:9090}")
    private String grpcAddress;

    private ManagedChannel channel;

    public AuthServiceGrpc.AuthServiceBlockingStub getAuthServiceBlockingStub() {
        if (channel == null) {
            channel = ManagedChannelBuilder.forTarget(grpcAddress)
                    .usePlaintext()
                    .build();
        }
        return AuthServiceGrpc.newBlockingStub(channel);
    }

    public ManagedChannel getChannel() {
        if (channel == null) {
            channel = ManagedChannelBuilder.forAddress(grpcAddress, 9090)
                    .usePlaintext()
                    .build();
        }
        return channel;
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            log.info("Shutting down gRPC channel for Auth service");
            channel.shutdown();
        }
    }
}

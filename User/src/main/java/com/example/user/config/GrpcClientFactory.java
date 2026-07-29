package com.example.user.config;

import com.example.grpc.auth.AuthServiceGrpc;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientFactory {

    private static final Logger log = LoggerFactory.getLogger(GrpcClientFactory.class);

    @Value("${spring.grpc.client.channels.auth-service.address}")
    private String grpcAddress;

    private final GrpcChannelFactory channelFactory;
    private ManagedChannel channel;

    public GrpcClientFactory(GrpcChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }

    public AuthServiceGrpc.AuthServiceBlockingStub getAuthServiceBlockingStub() {
        if (channel == null) {
            channel = channelFactory.createChannel(grpcAddress);
        }
        return AuthServiceGrpc.newBlockingStub(channel);
    }
}

package com.example.auth.config;

import com.example.user.grpc.UserServiceGrpc;
import io.grpc.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub usuarioServiceStub(GrpcChannelFactory channelFactory) {
        Channel channel = channelFactory.createChannel("usuarioService");
        return UserServiceGrpc.newBlockingStub(channel);
    }
}

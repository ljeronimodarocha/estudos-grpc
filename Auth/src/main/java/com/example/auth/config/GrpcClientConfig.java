package com.example.auth.config;

import com.example.grpc.user.UserServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Value("${spring.grpc.client.channels.usuarios-service.address}")
    private String urlUsuariosService;

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub usuarioServiceStub(GrpcChannelFactory channelFactory) {
        ManagedChannel local = channelFactory.createChannel(urlUsuariosService);
        return UserServiceGrpc.newBlockingStub(local);
    }
}

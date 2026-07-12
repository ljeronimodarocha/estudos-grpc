package com.example.bookapp.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    
    @Value("${spring.grpc.client.channels.auth-service.address}")
    private String authServiceAddress;
    
    @Bean
    public ManagedChannel authChannel() {
        String address = authServiceAddress.contains(":") ? authServiceAddress : authServiceAddress + ":9090";
        return ManagedChannelBuilder
            .forAddress(address.split(":")[0], Integer.parseInt(address.split(":")[1]))
            .usePlaintext()
            .build();
    }
}

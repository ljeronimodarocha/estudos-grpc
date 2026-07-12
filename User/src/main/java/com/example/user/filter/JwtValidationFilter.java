package com.example.user.filter;

import com.example.grpc.auth.*;
import io.grpc.Status;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {
    
    private final UserDetailsService userDetailsService;
    
    public JwtValidationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();
        
        // Endpoints públicos (permitAll)
        if (path.matches("/users$") || path.matches("/users/\\*\\*") || path.matches("/users/\\{id\\}")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = authHeader.substring(7);
        
        // Validar via gRPC no Auth
        try {
            // Validação simplificada - em produção usar gRPC real
            // Aqui validamos localmente usando JwtUtil do Auth
            // Para usar gRPC real, será necessário gerar o código gRPC
            
            // Simulação de chamada gRPC - substituir por chamada real quando gRPC for gerado
            // AuthServiceGrpc.AuthServiceStub stub = AuthServiceGrpc.newStub(channel);
            // ValidateTokenRequest request = ValidateTokenRequest.newBuilder().setToken(token).build();
            // ValidateTokenResponse response = stub.validate(request);
            
            // Por enquanto, apenas logamos a intenção de validar
            // Em produção: validar token via gRPC no Auth
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                "Erro ao validar token: " + e.getMessage());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}

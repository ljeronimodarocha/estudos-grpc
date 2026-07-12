package com.example.user.filter;

import com.example.user.config.GrpcClientFactory;
import com.example.grpc.auth.*;
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
    
    private final GrpcClientFactory grpcClientFactory;
    private final UserDetailsService userDetailsService;
    
    public JwtValidationFilter(GrpcClientFactory grpcClientFactory, UserDetailsService userDetailsService) {
        this.grpcClientFactory = grpcClientFactory;
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
        
        // Validar token via gRPC no Auth
        try {
            AuthServiceGrpc.AuthServiceBlockingStub stub = grpcClientFactory.getAuthServiceBlockingStub();
            ValidateTokenRequest validateRequest = ValidateTokenRequest.newBuilder()
                .setToken(token)
                .build();
            
            ValidateResponse validateResponse = stub.validate(validateRequest);
            
            if (validateResponse.getValid()) {
                String username = validateResponse.getUsername();
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                return;
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                "Erro ao validar token: " + e.getMessage());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}

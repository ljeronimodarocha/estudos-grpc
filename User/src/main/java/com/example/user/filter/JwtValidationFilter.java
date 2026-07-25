package com.example.user.filter;

import com.example.grpc.auth.AuthServiceGrpc;
import com.example.grpc.auth.ValidateResponse;
import com.example.grpc.auth.ValidateTokenRequest;
import com.example.user.config.GrpcClientFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtValidationFilter.class);
    private static final long GRPC_TIMEOUT_MS = 5000;

    private final GrpcClientFactory grpcClientFactory;

    public JwtValidationFilter(GrpcClientFactory grpcClientFactory) {
        this.grpcClientFactory = grpcClientFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            ValidateResponse validateResponse = validateTokenViaGrpc(token);

            if (validateResponse.getValid()) {
                String username = validateResponse.getUsername();
                authenticateUser(request, username);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                return;
            }

        } catch (Exception e) {
            log.error("Error validating token via gRPC", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro de autenticação");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private ValidateResponse validateTokenViaGrpc(String token) {
        log.debug("Calling Auth service via gRPC to validate token");
        AuthServiceGrpc.AuthServiceBlockingStub stub = grpcClientFactory.getAuthServiceBlockingStub()
            .withDeadlineAfter(GRPC_TIMEOUT_MS, TimeUnit.MILLISECONDS);

        ValidateTokenRequest validateRequest = ValidateTokenRequest.newBuilder()
            .setToken(token)
            .build();

        return stub.validate(validateRequest);
    }

    private void authenticateUser(HttpServletRequest request, String username) {
        log.info("Token validated successfully for user: {}", username);
        var authToken = new UsernamePasswordAuthenticationToken(username, null);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}

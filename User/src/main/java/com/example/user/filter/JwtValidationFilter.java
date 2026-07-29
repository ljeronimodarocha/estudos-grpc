package com.example.user.filter;

import com.example.grpc.auth.AuthServiceGrpc;
import com.example.grpc.auth.ValidateResponse;
import com.example.grpc.auth.ValidateTokenRequest;
import com.example.user.config.GrpcClientFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.Collection;
import java.util.Collections;
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

        String token = extractTokenFromCookie(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

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

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    if (!isSecureCookie(cookie)) {
                        log.warn("Cookie 'access_token' is not secure (missing HttpOnly or Secure flag)");
                    }
                    log.debug("Found access_token in cookie");
                    return cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("Falling back to Authorization header");
            return authHeader.substring(7);
        }

        return null;
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

    private boolean isSecureCookie(Cookie cookie) {
        try {
            return cookie.isHttpOnly()
                && cookie.getSecure();
        } catch (Exception e) {
            log.debug("Could not check cookie security attributes: {}", e.getMessage());
            return true;
        }
    }

    private void authenticateUser(HttpServletRequest request, String username) {
        log.info("Token validated successfully for user: {}", username);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}

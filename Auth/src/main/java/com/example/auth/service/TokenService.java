package com.example.auth.service;

import com.example.auth.model.Token;
import com.example.auth.model.UserAuthentication;
import com.example.auth.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token saveToken(UserAuthentication userAuthentication, String jwtToken, Token.TokenType type, long validitySeconds) {
        Token token = Token.builder()
                .token(jwtToken)
                .tokenType(type)
                .revoked(false)
                .expired(false)
                .userAuthentication(userAuthentication)
                .expiresAt(LocalDateTime.now().plusSeconds(validitySeconds))
                .build();
        return tokenRepository.save(token);
    }

    @Transactional
    public void revokeAllUserTokens(UserAuthentication userAuthentication) {
        List<Token> validTokens = tokenRepository.findAllByUserAuthenticationAndRevokedFalse(userAuthentication);
        validTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.flush();
    }

    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public boolean isTokenValid(String token) {
        return tokenRepository.findByTokenAndRevokedFalseAndExpiredFalse(token)
                .map(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Transactional
    public void revokeToken(Token token) {
        token.setRevoked(true);
        tokenRepository.save(token);
    }
}

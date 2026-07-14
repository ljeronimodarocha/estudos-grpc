package com.example.auth.service;

import com.example.auth.model.Token;
import com.example.auth.model.UserAuthentication;
import com.example.auth.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        // nothing
    }

    @Test
    void testSaveToken() {
        UserAuthentication user = UserAuthentication.builder().build();
        when(tokenRepository.save(any())).thenReturn(Token.builder().build());
        Token token = tokenService.saveToken(user, "jwt", Token.TokenType.ACCESS, 3600L);
        assertNotNull(token);
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void testRevokeAllUserTokens() {
        UserAuthentication user = mock(UserAuthentication.class);
        List<Token> tokens = List.of(mock(Token.class), mock(Token.class));
        when(tokenRepository.findAllByUserAuthenticationAndRevokedFalse(user)).thenReturn(tokens);
        tokenService.revokeAllUserTokens(user);
        tokens.forEach(t -> {
        });
        verify(tokenRepository).flush();
    }

    @Test
    void testFindByToken() {
        when(tokenRepository.findByToken("token")).thenReturn(Optional.of(mock(Token.class)));
        Optional<Token> result = tokenService.findByToken("token");
        assertTrue(result.isPresent());
    }

    @Test
    void testIsTokenValid() {
        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(2);
        Token token = Token.builder()
                .id(1L)
                .token("Test")
                .tokenType(Token.TokenType.ACCESS)
                .revoked(false)
                .expired(false).userAuthentication(UserAuthentication.builder().build())
                .createdAt(LocalDateTime.now())
                .expiresAt(expireAt)
                .build();
        when(tokenRepository.findByTokenAndRevokedFalseAndExpiredFalse("token")).thenReturn(Optional.of(token));
        boolean valid = tokenService.isTokenValid("token");
        assertTrue(valid);
    }

    @Test
    void testRevokeToken() {
        Token token = mock(Token.class);
        tokenService.revokeToken(token);
        {
        }
        verify(tokenRepository).save(token);
    }
}

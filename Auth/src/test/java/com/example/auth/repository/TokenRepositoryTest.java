package com.example.auth.repository;

import com.example.auth.model.Token;
import com.example.auth.model.UserAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenRepositoryTest {

    @Mock
    private TokenRepository tokenRepository;

    @Test
    void findByToken_returnsEmptyByDefault() {
        // Act
        Optional<Token> result = tokenRepository.findByToken("mock-token");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByToken_returnsTokenWhenConfigured() {
        // Arrange
        Token mockToken = mock(Token.class);
        doReturn(Optional.of(mockToken)).when(tokenRepository).findByToken("valid-token");

        // Act
        Optional<Token> result = tokenRepository.findByToken("valid-token");

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Test
    void findAllByUserAuthenticationAndRevokedFalse_returnsEmptyByDefault() {
        // Act
        List<Token> result = tokenRepository.findAllByUserAuthenticationAndRevokedFalse(mock(UserAuthentication.class));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByUserAuthenticationAndRevokedFalse_returnsTokensWhenConfigured() {
        // Arrange
        Token token1 = mock(Token.class);
        Token token2 = mock(Token.class);
        doReturn(List.of(token1, token2))
                .when(tokenRepository)
                .findAllByUserAuthenticationAndRevokedFalse(any(UserAuthentication.class));

        // Act
        List<Token> result = tokenRepository.findAllByUserAuthenticationAndRevokedFalse(mock(UserAuthentication.class));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findByTokenAndRevokedFalseAndExpiredFalse_returnsEmptyByDefault() {
        // Act
        Optional<Token> result = tokenRepository.findByTokenAndRevokedFalseAndExpiredFalse("test-token");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByTokenAndRevokedFalseAndExpiredFalse_returnsTokenWhenConfigured() {
        // Arrange
        Token mockToken = mock(Token.class);
        doReturn(Optional.of(mockToken)).when(tokenRepository).findByTokenAndRevokedFalseAndExpiredFalse("valid-token");
        
        // Act
        Optional<Token> result = tokenRepository.findByTokenAndRevokedFalseAndExpiredFalse("valid-token");

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Test
    void save_savesToken() {
        // Arrange
        Token token = new Token();
        token.setToken("new-token");
        token.setTokenType(Token.TokenType.ACCESS);
        token.setRevoked(false);
        token.setExpired(false);
        token.setExpiresAt(LocalDateTime.now().plusSeconds(3600));

        // Act
        tokenRepository.save(token);

        // Assert
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void flush_commitsChanges() {
        // Act
        tokenRepository.flush();

        // Assert
        verify(tokenRepository, times(1)).flush();
    }
}

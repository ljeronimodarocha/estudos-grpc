package com.example.auth.repository;

import com.example.auth.model.Token;
import com.example.auth.model.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    List<Token> findAllByUserAuthenticationAndRevokedFalse(UserAuthentication userAuthentication);
    Optional<Token> findByTokenAndRevokedFalseAndExpiredFalse(String token);
}

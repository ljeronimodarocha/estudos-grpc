package com.example.auth.repository;

import com.example.auth.model.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryAuthentication extends JpaRepository<UserAuthentication, Long> {
    Optional<UserAuthentication> findByUsername(String username);
    boolean existsByUsername(String username);
}

package com.example.auth.service;

import com.example.auth.dto.RegisterRequest;
import com.example.auth.model.UserAuthentication;
import com.example.auth.repository.UserRepositoryAuthentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceAuth implements UserDetailsService {

    private final UserRepositoryAuthentication userRepositoryAuthentication;
    private final PasswordEncoder passwordEncoder;

    public UserServiceAuth(UserRepositoryAuthentication userRepositoryAuthentication, PasswordEncoder passwordEncoder) {
        this.userRepositoryAuthentication = userRepositoryAuthentication;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuthentication userAuthentication = userRepositoryAuthentication.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(userAuthentication.getUsername())
                .password(userAuthentication.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }

    public UserAuthentication registerUserAuthentication(RegisterRequest request, Long userId) {
        if (userRepositoryAuthentication.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserAuthentication userAuthentication = UserAuthentication.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .userId(userId)
                .build();

        return userRepositoryAuthentication.save(userAuthentication);
    }

    public UserAuthentication findByUsername(String username) {
        return userRepositoryAuthentication.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}

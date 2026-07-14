package com.example.auth.service;

import com.example.auth.dto.RegisterRequest;
import com.example.auth.model.UserAuthentication;
import com.example.auth.repository.UserRepositoryAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class UserServiceAuthTest {
    @Mock
    private UserRepositoryAuthentication userRepositoryAuthentication;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceAuth userServiceAuth;

    @BeforeEach
    void setUp() {
        // nothing
    }

    @Test
    void testLoadUserByUsername() {
        UserAuthentication userAuth = UserAuthentication.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .username("testUser")
                .password("test")
                .build();
        userAuth.setId(1L);

        when(userRepositoryAuthentication.findByUsername(anyString())).thenReturn(Optional.of(userAuth));
        UserDetails result = userServiceAuth.loadUserByUsername("testuser");
        assertNotNull(result);
        verify(userRepositoryAuthentication).findByUsername("testuser");
    }

    @Test
    void testRegisterUserAuthentication() {
        RegisterRequest request = new RegisterRequest("newuser", "pass", "email", "display");
        when(userRepositoryAuthentication.existsByUsername("newuser")).thenReturn(false);
        UserAuthentication saved = mock(UserAuthentication.class);
        when(userRepositoryAuthentication.save(any(UserAuthentication.class))).thenReturn(saved);
        UserAuthentication result = userServiceAuth.registerUserAuthentication(request, 1L);
        assertNotNull(result);
        verify(userRepositoryAuthentication).existsByUsername("newuser");
    }

    @Test
    void testFindByUsername() {
        UserAuthentication userAuth = mock(UserAuthentication.class);
        when(userRepositoryAuthentication.findByUsername("user")).thenReturn(Optional.of(userAuth));
        UserAuthentication result = userServiceAuth.findByUsername("user");
        assertNotNull(result);
    }
}

package com.example.user.controller;

import com.example.user.dto.RegisterRequest;
import com.example.user.model.User;
import com.example.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "New User");
        User savedUser = User.builder()
                .name("newuser")
                .email("new@example.com")
                .displayName("New User")
                .enabled(true)
                .id(1L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.registerUser(request)).thenReturn(savedUser);

        ResponseEntity<User> response = userController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("newuser");
        assertThat(response.getBody().getEmail()).isEqualTo("new@example.com");
        verify(userService, times(1)).registerUser(any(RegisterRequest.class));
    }

    @Test
    void register_createsCorrectUri() {
        RegisterRequest request = new RegisterRequest("user1", "user1@test.com", "User 1");
        User savedUser = User.builder()
                .name("user1")
                .email("user1@test.com")
                .displayName("User 1")
                .enabled(true)
                .id(42L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.registerUser(request)).thenReturn(savedUser);

        ResponseEntity<User> response = userController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().toString()).isEqualTo("/users/42");
    }

    @Test
    void me_success() {
        User user = User.builder()
                .name("authenticateduser")
                .email("auth@example.com")
                .displayName("Auth User")
                .enabled(true)
                .id(1L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.findByName(anyString())).thenReturn(user);

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("authenticateduser");
        
        ResponseEntity<User> response = userController.me(mockAuth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("authenticateduser");
        verify(userService, times(1)).findByName(anyString());
    }

    @Test
    void me_userNotFound_throwsException() {
        when(userService.findByName("nonexistent")).thenThrow(
                new UsernameNotFoundException("User not found: nonexistent"));

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("nonexistent");
        
        assertThatThrownBy(() -> userController.me(mockAuth))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getUserById_success() {
        User user = User.builder()
                .name("user42")
                .email("user42@example.com")
                .displayName("User 42")
                .enabled(true)
                .id(42L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userService.findById(42L)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("user42");
        assertThat(response.getBody().getId()).isEqualTo(42L);
        verify(userService, times(1)).findById(42L);
    }

    @Test
    void getUserById_notFound_returns404() {
        when(userService.findById(999L)).thenThrow(new RuntimeException("User not found: 999"));

        assertThatThrownBy(() -> userController.getUserById(999L))
                .isInstanceOf(RuntimeException.class);
    }
}

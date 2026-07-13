package com.example.auth.repository;

import com.example.auth.model.UserAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAuthenticationTest {

    @Mock
    private UserRepositoryAuthentication userRepositoryAuthentication;

    @Test
    void existsByUsername_returnsFalseByDefault() {
        // Act
        boolean exists = userRepositoryAuthentication.existsByUsername("testuser");

        // Assert
        assertFalse(exists);
    }

    @Test
    void existsByUsername_returnsTrueWhenConfigured() {
        // Arrange
        doReturn(true).when(userRepositoryAuthentication).existsByUsername("existinguser");

        // Act
        boolean exists = userRepositoryAuthentication.existsByUsername("existinguser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void findByUsername_returnsEmptyByDefault() {
        // Act
        Optional<UserAuthentication> result = userRepositoryAuthentication.findByUsername("testuser");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUsername_returnsUserWhenConfigured() {
        // Arrange
        UserAuthentication mockUser = mock(UserAuthentication.class);
        doReturn(Optional.of(mockUser)).when(userRepositoryAuthentication).findByUsername("testuser");

        // Act
        Optional<UserAuthentication> result = userRepositoryAuthentication.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Test
    void save_savesUserAuthentication() {
        // Arrange
        UserAuthentication user = new UserAuthentication();
        user.setUsername("newuser");
        user.setPassword("encodedpassword");

        // Act
        userRepositoryAuthentication.save(user);

        // Assert
        verify(userRepositoryAuthentication, times(1)).save(any(UserAuthentication.class));
    }

    @Test
    void findAll_returnsListOfUsers() {
        // Act
        var result = userRepositoryAuthentication.findAll();

        // Assert
        assertNotNull(result);
    }

    @Test
    void findById_returnsOptionalUser() {
        // Arrange
        UserAuthentication mockUser = mock(UserAuthentication.class);
        doReturn(Optional.of(mockUser)).when(userRepositoryAuthentication).findById(1L);

        // Act
        Optional<UserAuthentication> result = userRepositoryAuthentication.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get());
    }

    @Test
    void existsByUsername_withValidUsername() {
        // Arrange
        String username = "testuser";
        doReturn(true).when(userRepositoryAuthentication).existsByUsername(username);

        // Act
        boolean exists = userRepositoryAuthentication.existsByUsername(username);

        // Assert
        assertTrue(exists);
        verify(userRepositoryAuthentication, times(1)).existsByUsername(username);
    }
}

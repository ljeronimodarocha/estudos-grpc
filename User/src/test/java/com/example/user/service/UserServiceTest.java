package com.example.user.service;

import com.example.user.dto.RegisterRequest;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        lenient().when(userRepository.findByName("testuser")).thenReturn(Optional.empty());
        lenient().when(userRepository.existsByName("testuser")).thenReturn(false);
        lenient().when(userRepository.existsByName("existinguser")).thenReturn(true);
    }

    @Test
    void registerUser_success() {
        // Arrange
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

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getDisplayName()).isEqualTo("New User");
        assertThat(result.isEnabled()).isTrue();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_savesWithDefaults() {
        // Arrange
        RegisterRequest request = new RegisterRequest("user2", "user2@example.com", null);

        User savedUser = User.builder()
                .name("user2")
                .email("user2@example.com")
                .enabled(true)
                .id(2L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(request);

        // Assert
        assertThat(result.getName()).isEqualTo("user2");
        assertThat(result.isEnabled()).isTrue();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findByName_success() {
        // Arrange
        User user = User.builder()
                .name("testuser")
                .email("test@example.com")
                .enabled(true)
                .id(1L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByName("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findByName("testuser");
    }

    @Test
    void findByName_notFound_throwsException() {
        // Arrange
        when(userRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findByName("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: nonexistent");

        verify(userRepository, times(1)).findByName("nonexistent");
    }

    @Test
    void findById_success() {
        // Arrange
        User user = User.builder()
                .name("testuser")
                .email("test@example.com")
                .enabled(true)
                .id(42L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findById(42L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getName()).isEqualTo("testuser");
        verify(userRepository, times(1)).findById(42L);
    }

    @Test
    void findById_notFound_throwsException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found: 999");

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void findByIdentity_success() {
        // Arrange
        User user = User.builder()
                .name("testuser")
                .email("test@example.com")
                .enabled(true)
                .id(7L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByIdentity("7");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getName()).isEqualTo("testuser");
        verify(userRepository, times(1)).findById(7L);
    }

    @Test
    void findByIdentity_invalidFormat_throwsException() {
        // Act & Assert
        assertThatThrownBy(() -> userService.findByIdentity("abc"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid user ID format");

        verify(userRepository, never()).findById(any(Long.class));
    }

    @Test
    void findByIdentity_notFound_throwsException() {
        // Arrange
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findByIdentity("100"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found: 100");

        verify(userRepository, times(1)).findById(100L);
    }

    @Test
    void existsByName_true() {
        // Act
        boolean result = userService.existsByName("existinguser");

        // Assert
        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsByName("existinguser");
    }

    @Test
    void existsByName_false() {
        // Act
        boolean result = userService.existsByName("nonexistent");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository, times(1)).existsByName("nonexistent");
    }

    @Test
    void loadUserByUsername_success() {
        // Arrange
        User user = User.builder()
                .name("testuser")
                .email("test@example.com")
                .enabled(true)
                .id(1L)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));

        // Act
        User result = userService.loadUserByUsername("testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("testuser");
    }

    @Test
    void loadUserByUsername_notFound_throwsException() {
        // Arrange
        when(userRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: nonexistent");
    }
}

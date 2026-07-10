package com.example.user.service;

import com.example.user.dto.RegisterRequest;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loadUserByUsername(String name) throws UsernameNotFoundException {
        return userRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + name));

    }

    public User registerUser(RegisterRequest request) {
      /*  if (userRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }*/

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .displayName(request.displayName())
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    public User findByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + name));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }
}

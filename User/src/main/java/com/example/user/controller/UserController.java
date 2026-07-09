package com.example.user.controller;

import com.example.user.dto.RegisterRequest;
import com.example.user.model.User;
import com.example.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = this.userService.registerUser(request);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
    }

    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication authentication) {
        User user = userService.findByName(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}

package com.example.bookapp.filter;

public class UserAuthenticationDetails {

    private final Long userId;

    public UserAuthenticationDetails(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}

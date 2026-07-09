package com.example.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordEncoderUtil {

    private PasswordEncoderUtil() {}

    public static String encode(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
    }
}

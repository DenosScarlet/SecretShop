package com.secretshop.shop.util;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtUtils {

    private final JwtDecoder jwtDecoder;

    public JwtUtils(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public UUID extractUserId(String authHeader) throws RuntimeException {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Invalid Authorization header");
            }

            String token = authHeader.substring(7);
            Jwt jwt = jwtDecoder.decode(token);

            if (jwt.getSubject() == null) {
                throw new RuntimeException("User ID not found in token");
            }

            return UUID.fromString(jwt.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract user ID: " + e.getMessage());
        }
    }
}

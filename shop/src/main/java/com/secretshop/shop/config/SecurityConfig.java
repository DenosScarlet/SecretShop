package com.secretshop.shop.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                        .bearerTokenResolver(this::resolveTokenFromQueryParam)
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }


    // Кастомный резолвер для токена в query-параметре
    private String resolveTokenFromQueryParam(HttpServletRequest request) {
        String token = request.getParameter("access_token");
        if (token != null && !token.isBlank()) {
            return token;
        }
        return null; // Вернет 401 если токен не найден
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Указываем JWKS URI из Keycloak
        return NimbusJwtDecoder.withJwkSetUri(
                "http://localhost:8180/realms/secretshoprealm/protocol/openid-connect/certs"
        ).build();
    }
}
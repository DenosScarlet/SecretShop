package com.secretshop.keycloak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * SecurityConfig class configures security settings for the application,
 * enabling security filters and setting up OAuth2 login and logout behavior.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * Configures the security filter chain for handling HTTP requests, OAuth2 login, and logout.
     *
     * @param http HttpSecurity object to define web-based security at the HTTP level
     * @return SecurityFilterChain for filtering and securing HTTP requests
     * @throws Exception in case of an error during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configures authorization rules for different endpoints
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/").permitAll() // Allows public access to the root URL
                        .requestMatchers("/work_env").authenticated() // Requires authentication to access "/work_env"
                        .anyRequest().authenticated() // Requires authentication for any other request
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())) // Указываем JwtDecoder напрямую
                )
                // Configures OAuth2 login settings
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/keycloak") // Sets custom login page for OAuth2 with Keycloak
                        .defaultSuccessUrl("/work_env", true) // Redirects to "/work_env" after successful login
                )
                // Configures logout settings
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        successHandler.setPostLogoutRedirectUri("{baseUrl}/");
        return successHandler;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8180/realms/secretshoprealm/protocol/openid-connect/certs")
//        return NimbusJwtDecoder.withJwkSetUri("http://keycloak:8080/realms/secretshoprealm/protocol/openid-connect/certs")
                .build();
    }
}
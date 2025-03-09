package com.secretshop.keycloak.config;

import static org.springframework.security.config.Customizer.withDefaults;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler) throws Exception {
        return http.authorizeHttpRequests(authorise ->
                        authorise
                                .requestMatchers("/static/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(authorise -> authorise.accessDeniedPage("/access-denied"))
                .oauth2Login(withDefaults())
                .logout(logout ->
                        logout.logoutSuccessHandler(oidcLogoutSuccessHandler)).build();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {

        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
                    Map<String, Object> realmAccess = userInfo.getClaim("realm_access");
                    Collection<String> realmRoles;
                    if (realmAccess != null
                            && (realmRoles = (Collection<String>) realmAccess.get("roles")) != null) {
                        realmRoles
                                .forEach(role -> mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                }
            });
            return mappedAuthorities;
        };
    }

    @Bean
    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri(URI.create("http://localhost:8080").toString());
        return successHandler;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.keycloakClientRegistration());
    }

    private ClientRegistration keycloakClientRegistration() {
        return ClientRegistration.withRegistrationId("keycloak")
                .clientId("myclient")
                .clientSecret("GNOOFsIDrhZhIHLP3GHDyBQ5uMEQK0SK")
                .scope("openid", "profile")
                .authorizationUri("http://localhost:8180/auth/realms/secretshoprealm/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8180/auth/realms/secretshoprealm/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8180/auth/realms/secretshoprealm/protocol/openid-connect/userinfo")
                .userNameAttributeName("preferred_username")
                .clientName("Keycloak")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .build();
    }

} 
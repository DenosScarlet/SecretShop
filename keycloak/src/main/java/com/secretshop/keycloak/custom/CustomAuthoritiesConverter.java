package com.secretshop.keycloak.custom;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger LOGGER = Logger.getLogger(CustomAuthoritiesConverter.class.getName());
    private final String clientId;

    public CustomAuthoritiesConverter(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Извлечение realm_access.roles
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            if (realmRoles != null) {
                for (String role : realmRoles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                }
            }
        }

        // Извлечение resource_access.<clientId>.roles
        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
            if (clientAccess != null && clientAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> clientRoles = (List<String>) clientAccess.get("roles");
                if (clientRoles != null) {
                    for (String role : clientRoles) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    }
                }
            }
        }

        // Дополнительное логирование для диагностики
        LOGGER.info("JWT Claims: " + jwt.getClaims());
        LOGGER.info("Realm roles: " + (realmAccess != null ? realmAccess.get("roles") : "null"));
        LOGGER.info("Resource roles for client " + clientId + ": " +
                (resourceAccess != null && resourceAccess.containsKey(clientId) ?
                        resourceAccess.get(clientId) : "null"));
        LOGGER.info("Extracted authorities: " + authorities);

        return authorities;
    }
}
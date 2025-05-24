package com.secretshop.keycloak.custom;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import java.util.ArrayList;
import java.util.Collection;

public class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final JwtGrantedAuthoritiesConverter realmRolesConverter;
    private final JwtGrantedAuthoritiesConverter clientRolesConverter;

    public CustomAuthoritiesConverter(String clientId) {
        this.realmRolesConverter = new JwtGrantedAuthoritiesConverter();
        realmRolesConverter.setAuthorityPrefix(""); // Без префикса!
        realmRolesConverter.setAuthoritiesClaimName("realm_access.roles");

        this.clientRolesConverter = new JwtGrantedAuthoritiesConverter();
        clientRolesConverter.setAuthorityPrefix(""); // Без префикса!
        clientRolesConverter.setAuthoritiesClaimName("resource_access." + clientId + ".roles");
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(realmRolesConverter.convert(jwt));
        authorities.addAll(clientRolesConverter.convert(jwt));
        System.out.println("Authorities: " + authorities); // Для отладки
        return authorities;
    }
}


package com.secretshop.keycloak.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class KeycloakUserResponse {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}

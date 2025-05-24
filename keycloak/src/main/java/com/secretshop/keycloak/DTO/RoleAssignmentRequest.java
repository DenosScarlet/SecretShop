package com.secretshop.keycloak.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RoleAssignmentRequest {
    @NotBlank
    private String roleName;
}

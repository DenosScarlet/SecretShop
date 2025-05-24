package com.secretshop.keycloak.DTO;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class PasswordResetRequest {
    @NotBlank
    private String newPassword;
}

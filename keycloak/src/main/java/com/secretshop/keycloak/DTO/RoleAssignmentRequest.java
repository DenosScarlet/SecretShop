package com.secretshop.keycloak.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "Запрос на назначение роли пользователю")
public class RoleAssignmentRequest {
    @Schema(description = "Название роли", requiredMode = Schema.RequiredMode.REQUIRED, example = "ROLE_ADMIN")
    @NotBlank
    private String roleName;
}

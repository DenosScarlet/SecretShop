package com.secretshop.keycloak.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления аватара пользователя")
public class AvatarUpdateDTO {
    @Schema(description = "Новый URL аватара", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "https://example.com/new-avatar.jpg")
    private String avatarUrl;
}

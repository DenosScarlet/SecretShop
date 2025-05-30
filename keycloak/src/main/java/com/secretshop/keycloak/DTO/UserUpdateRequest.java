package com.secretshop.keycloak.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление данных пользователя")
public class UserUpdateRequest {
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Петров")
    private String lastName;

    @Schema(description = "Отчество пользователя", example = "Сергеевич")
    private String middleName;

    @Schema(description = "URL аватара пользователя", example = "https://example.com/avatar.jpg")
    private String avatar;
}

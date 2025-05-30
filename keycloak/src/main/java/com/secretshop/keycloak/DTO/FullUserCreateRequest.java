package com.secretshop.keycloak.DTO;

import com.secretshop.keycloak.enums.WorkGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание нового пользователя")
public class FullUserCreateRequest {
    @Schema(description = "Имя пользователя (логин)", requiredMode = Schema.RequiredMode.REQUIRED, example = "user123")
    @NotBlank
    private String username;

    @Schema(description = "Email пользователя", example = "user@example.com")
    @Email
    private String email;

    @Schema(description = "Имя пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "Иван")
    @NotBlank
    private String firstName;

    @Schema(description = "Фамилия пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "Петров")
    @NotBlank
    private String lastName;

    @Schema(description = "Пароль пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "securePassword123")
    @NotBlank
    private String password;

    @Schema(description = "URL аватара пользователя", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "Отчество пользователя", example = "Сергеевич")
    private String middleName;

    @Schema(description = "Рабочая группа пользователя", example = "DEVELOPMENT")
    private WorkGroup workGroup;

    @Schema(description = "Начальный баланс пользователя", example = "0")
    private Integer balance = 0;
}

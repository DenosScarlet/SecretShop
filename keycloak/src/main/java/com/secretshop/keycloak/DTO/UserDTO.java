package com.secretshop.keycloak.DTO;

import com.secretshop.keycloak.enums.WorkGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO пользователя системы")
public class UserDTO {
    @Schema(description = "Уникальный идентификатор пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "URL аватара пользователя", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Петров")
    private String lastName;

    @Schema(description = "Отчество пользователя", example = "Сергеевич")
    private String middleName;

    @Schema(description = "Рабочая группа пользователя", example = "DEVELOPMENT")
    private WorkGroup workGroup;

    @Schema(description = "Баланс пользователя", example = "1000")
    private Integer balance;
}

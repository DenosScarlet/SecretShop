package com.secretshop.keycloak.DTO;

import com.secretshop.keycloak.enums.WorkGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления основных данных пользователя")
public class UserUpdateDTO {
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Петров")
    private String lastName;

    @Schema(description = "Отчество пользователя", example = "Сергеевич")
    private String middleName;

    @Schema(description = "Рабочая группа пользователя", example = "DEVELOPMENT")
    private WorkGroup workGroup;
}

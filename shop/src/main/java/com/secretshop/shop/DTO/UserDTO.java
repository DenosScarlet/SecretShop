package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.WorkGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "DTO пользователя системы")
public class UserDTO implements Serializable {
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Петров")
    private String lastName;

    @Schema(description = "Отчество пользователя", example = "Сергеевич")
    private String midleName;

    @Schema(description = "Рабочая группа пользователя", example = "DEVELOPMENT")
    private WorkGroup workGroup;

    @Schema(description = "Баланс пользователя", example = "1000")
    private Integer balance;
}

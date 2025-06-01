package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.WorkGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных сотрудника")
public class UserDTO {
    @Schema(description = "Уникальный идентификатор сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Ссылка на аватар", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "Имя сотрудника", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия сотрудника", example = "Иванов")
    private String lastName;

    @Schema(description = "Отчество сотрудника", example = "Иванович")
    private String middleName;

    @Schema(description = "Рабочая группа сотрудника")
    private WorkGroup workGroup;

    @Schema(description = "Баланс сотрудника", example = "1000")
    private Integer balance;
}

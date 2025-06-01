package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO связи сотрудника и квеста")
public class UsersQuestDTO {
    @Schema(description = "Уникальный идентификатор связи", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID usersQuestId;

    @Schema(description = "ID сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID usersId;

    @Schema(description = "ID квеста", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID questId;

    @Schema(description = "Статус выполнения квеста")
    private Status questStatus;

    @Schema(description = "Количество выполненных шагов", example = "2")
    private Integer completedSteps;
}

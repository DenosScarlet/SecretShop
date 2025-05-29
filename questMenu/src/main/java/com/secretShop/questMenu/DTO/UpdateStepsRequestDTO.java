package com.secretShop.questMenu.DTO;

import com.secretShop.questMenu.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на обновление шагов выполнения квеста")
public class UpdateStepsRequestDTO {
    @Schema(description = "ID сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "ID квеста", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID questId;

    @Schema(description = "Новое значение выполненных шагов", example = "4")
    private Integer newStepsValue;

    @Schema(description = "Новый статус квеста")
    private Status questStatus;
}

package com.secretShop.questMenu.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с информацией о шагах выполнения квеста")
public class StepsResponseDTO {
    @Schema(description = "ID сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "ID квеста", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID questId;

    @Schema(description = "Количество выполненных шагов", example = "3")
    private Integer completedSteps;

    @Schema(description = "Всего шагов для выполнения", example = "5")
    private Integer stepsToComplete;
}
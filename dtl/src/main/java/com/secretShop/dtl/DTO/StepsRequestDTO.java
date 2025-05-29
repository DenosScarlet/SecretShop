package com.secretShop.dtl.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос информации о шагах выполнения квеста")
public class StepsRequestDTO {
    @Schema(description = "ID сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "ID квеста", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID questId;
}

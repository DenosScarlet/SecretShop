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
@Schema(description = "DTO для обновления статуса квеста")
public class QuestStatusDTO {
    @Schema(description = "ID квеста", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID questId;

    @Schema(description = "Статус квеста")
    private Status questStatus;

    @Schema(description = "Количество выполненных шагов", example = "3")
    private Integer completedSteps;
}

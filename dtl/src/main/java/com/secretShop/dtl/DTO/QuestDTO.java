package com.secretShop.dtl.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secretShop.dtl.enums.Frequency;
import com.secretShop.dtl.enums.WorkGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Объект передачи данных для квеста")
public class QuestDTO implements Serializable {
    @Schema(description = "Уникальный идентификатор квеста", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID questId;

    @Schema(description = "Название квеста", example = "Обработка документов")
    private String questTitle;

    @Schema(description = "Описание квеста", example = "Провести тщательный анализ документа с исправлением опечаток")
    private String description;

    @Schema(description = "Количество шагов для завершения", example = "5")
    private Integer stepsToComplete;

    @Schema(description = "Частота выполнения квеста")
    private Frequency frequency;

    @Schema(description = "Рабочая группа сотрудника")
    private WorkGroup workGroup;

    @Schema(description = "Дата начала квеста", example = "2025-06-01T10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "Дата окончания квеста", example = "2025-06-30T23:59:59")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(description = "Награда за квест в монетах", example = "100")
    private Integer cost;
}
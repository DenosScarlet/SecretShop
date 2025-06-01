package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для операции над товаром")
public class OperationOnItemDTO {
    @Schema(description = "Уникальный идентификатор операции", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID operationsId;

    @Schema(description = "ID сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "ID предмета", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID itemId;

    @Schema(description = "Статус операции")
    private Status status;

    @Schema(description = "История операции", example = "Продажа товара")
    private String operationHistory;
}

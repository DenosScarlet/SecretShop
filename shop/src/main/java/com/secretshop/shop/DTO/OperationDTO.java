package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Модель операции (покупка/продажа)")
public class OperationDTO {
    @Schema(description = "ID операции", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID operationsId;

    @Schema(description = "ID пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "ID товара", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID itemId;

    @Schema(description = "Статус операции", example = "COMPLETED")
    private Status status;

    @Schema(description = "История операции", example = "Покупка успешно завершена")
    private String operationHistory;
}

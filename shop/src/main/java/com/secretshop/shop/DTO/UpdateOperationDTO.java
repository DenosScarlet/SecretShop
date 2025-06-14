package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Schema(description = "DTO для обновления операции")
public class UpdateOperationDTO implements Serializable {
    @Schema(description = "Новый статус операции",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "COMPLETED")
    private Status status;

    @Schema(description = "История изменений операции",
            example = "Статус изменен на COMPLETED")
    private String operationHistory;
}

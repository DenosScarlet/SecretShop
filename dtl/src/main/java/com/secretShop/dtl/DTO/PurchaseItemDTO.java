package com.secretShop.dtl.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для покупки товара")
public class PurchaseItemDTO {
    @Schema(description = "ID сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "ID товара", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID itemId;
}

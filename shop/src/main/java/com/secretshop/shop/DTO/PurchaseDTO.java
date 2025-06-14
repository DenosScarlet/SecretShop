package com.secretshop.shop.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель запроса на покупку")
public class PurchaseDTO implements Serializable {
    @Schema(description = "ID товара", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID itemId;

    @Schema(description = "ID пользователя", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;
}

package com.secretShop.dtl.DTO;


import com.secretShop.dtl.enums.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для представления товара")
public class ItemDTO {
    @Schema(description = "Уникальный идентификатор товара", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID itemId;

    @Schema(description = "Название товара", example = "Планшет")
    private String itemName;

    @Schema(description = "Описание товара", example = "iShpak 18")
    private String description;

    @Schema(description = "Владелец товара", example = "SecretShop")
    private String owner;

    @Schema(description = "Тип товара")
    private Type type;

    @Schema(description = "Стоимость товара", example = "150")
    private Integer cost;

    @Schema(description = "Количество товаров", example = "5")
    private Integer count;
}

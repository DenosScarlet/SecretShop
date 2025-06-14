package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель товара")
public class ItemDTO implements Serializable {
    @Schema(description = "Уникальный идентификатор товара", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID itemId;

    @Schema(description = "Название товара", example = "Книга по Java")
    private String itemName;

    @Schema(description = "Описание товара", example = "Учебник по Spring Boot")
    private String description;

    @Schema(description = "Владелец товара", example = "user123")
    private String owner;

    @Schema(description = "Тип товара", example = "BOOK")
    private Type type;

    @Schema(description = "Цена товара", example = "1000")
    private Integer cost;

    @Schema(description = "Количество товара", example = "5")
    private Integer count;
}

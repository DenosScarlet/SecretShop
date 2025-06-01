package com.secretShop.dtl.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип предмета в магазине")
public enum Type {
    @Schema(description = "Мерч (фирменная продукция)")
    MERCH,

    @Schema(description = "Электронные устройства")
    DEVICE,

    @Schema(description = "Аксессуары")
    ACCESSORIES,

    @Schema(description = "Купоны и промо-коды")
    COUPONS
}

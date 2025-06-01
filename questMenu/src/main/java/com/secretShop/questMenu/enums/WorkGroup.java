package com.secretShop.questMenu.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Рабочая группа")
public enum WorkGroup {
    @Schema(description = "Разработка") DEVELOPMENT,
    @Schema(description = "Обработка данных") DATA_PROCESSING,
    @Schema(description = "Менеджмент") MANAGEMENT
}

package com.secretShop.questMenu.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Частота обновления квеста")
public enum Frequency {
    @Schema(description = "Ежедневный") DAILY,
    @Schema(description = "Еженедельный") WEEKLY,
    @Schema(description = "Ежемесячный") MONTHLY,
    @Schema(description = "Ежегодный") ANNUAL,
    @Schema(description = "Однократный") ONCE
}

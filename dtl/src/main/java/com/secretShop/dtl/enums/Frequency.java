package com.secretShop.dtl.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Частота выполнения квеста")
public enum Frequency {
    @Schema(description = "Ежедневный") DAILY,
    @Schema(description = "Еженедельный") WEEKLY,
    @Schema(description = "Ежемесячный") MONTHLY,
    @Schema(description = "Ежегодный") ANNUAL,
    @Schema(description = "Однократный") ONCE
}

package com.secretShop.dtl.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус выполнения квеста")
public enum Status {
    @Schema(description = "В процессе выполнения") IN_PROGRESS,
    @Schema(description = "Завершен") COMPLETE
}

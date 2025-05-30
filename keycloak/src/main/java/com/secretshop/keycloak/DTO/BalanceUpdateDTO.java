package com.secretshop.keycloak.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления баланса пользователя")
public class BalanceUpdateDTO {
    @Schema(description = "Новое значение баланса", requiredMode = Schema.RequiredMode.REQUIRED, example = "1500")
    private Integer newBalance;
}

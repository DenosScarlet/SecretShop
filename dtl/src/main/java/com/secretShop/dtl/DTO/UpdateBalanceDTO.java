package com.secretShop.dtl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceDTO {
    private UUID userId;
    private Integer newBalance;
}

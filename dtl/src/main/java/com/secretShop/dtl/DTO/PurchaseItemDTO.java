package com.secretShop.dtl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDTO {
    private UUID userId;
    private UUID itemId;
}

package com.secretShop.dtl.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepsRequestDTO {
    private UUID userId;
    private UUID questId;
}

package com.secretShop.dtl.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStepsRequestDTO {
    private UUID userId;
    private UUID questId;
    private Integer newStepsValue;
}

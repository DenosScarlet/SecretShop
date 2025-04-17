package com.secretShop.questMenu.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepsResponseDTO {
    private UUID userId;
    private UUID questId;
    private Integer completedSteps;
    private Integer stepsToComplete;
}
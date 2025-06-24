package com.secretShop.questMenu.DTO;

import com.secretShop.questMenu.enums.Frequency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO связи сотрудника и квеста")
public class UsersQuestsDTO {
    private UUID questId;
    private String questTitle;
    private Integer stepsToComplete;
    private Integer completedSteps;
    private Frequency frequency;
    private String questStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer cost;
}
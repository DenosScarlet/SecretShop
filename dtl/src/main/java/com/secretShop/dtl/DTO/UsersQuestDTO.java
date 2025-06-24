package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.Frequency;
import com.secretShop.dtl.enums.Status;
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
public class UsersQuestDTO {
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

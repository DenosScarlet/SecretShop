package com.secretShop.dtl.service.dto;

import com.secretShop.dtl.enums.Frequency;
import com.secretShop.dtl.enums.WorkGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestDTO {
    private UUID questId;
    private String questTitle;
    private String description;
    private Integer stepsToComplete;
    private Frequency frequency;
    private WorkGroup workGroup;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer cost;
}

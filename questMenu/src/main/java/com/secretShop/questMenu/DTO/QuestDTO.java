package com.secretShop.questMenu.DTO;

import com.secretShop.questMenu.enums.Frequency;
import com.secretShop.questMenu.enums.WorkGroup;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestDTO {
    private String questTitle;
    private String description;
    private Frequency Frequency;
    private WorkGroup workGroup;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer cost;
}

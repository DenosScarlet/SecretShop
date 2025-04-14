package com.secretShop.questMenu.DTO;

import com.secretShop.questMenu.enums.Frequency;
import com.secretShop.questMenu.enums.WorkGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestDTO {
    private UUID quest_id;
    private String quest_title;
    private String description;
    private Integer steps_to_complete;
    private Frequency frequency;
    private WorkGroup work_group;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private Integer cost;
}

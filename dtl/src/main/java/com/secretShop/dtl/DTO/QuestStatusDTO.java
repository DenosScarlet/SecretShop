package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestStatusDTO {
    private UUID questId;
    private Status questStatus;
    private Integer completedSteps;
}

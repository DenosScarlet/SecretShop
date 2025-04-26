package com.secretShop.questMenu.DTO;

import com.secretShop.questMenu.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersQuestDTO {
    private UUID usersQuestId;
    private UUID usersId;
    private UUID questId;
    private Status questStatus;
    private Integer completedSteps;
}
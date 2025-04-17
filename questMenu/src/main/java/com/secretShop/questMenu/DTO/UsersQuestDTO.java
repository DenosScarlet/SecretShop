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
    private UUID users_quest_id;
    private UUID users_id;
    private UUID quest_id;
    private Status quest_status;
    private Integer completed_steps;
}
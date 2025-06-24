package com.secretShop.questMenu.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestDTO {
    private UUID questId;
    private QuestDTO questDTO;
}

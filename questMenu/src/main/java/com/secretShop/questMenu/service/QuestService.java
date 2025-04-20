package com.secretShop.questMenu.service;

import com.secretShop.questMenu.DTO.StepsRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QuestService {
    QuestClient questClient = new QuestClient();
    @Transactional
    public void updateCompletedSteps(UUID userId, UUID questId) {
        Integer stepsToComplete = questClient.getSteps(userId, questId).getStepsToComplete();

    }
}

package com.secretShop.questMenu.service;

import com.secretShop.questMenu.DTO.*;
import com.secretShop.questMenu.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestService {
    //QuestClient questClient = new QuestClient();
    private final KafkaQuestClient questClient;

    @Transactional
    public void updateSteps(StepsRequestDTO stepsRequest) {

        Status currentStatus = Status.valueOf(questClient.getQuestStatusById(stepsRequest.getUserId(), stepsRequest.getQuestId()));

        // Если квест уже завершен - выходим из метода
        if (currentStatus == Status.COMPLETE) {
            return;
        }

        UUID userId = stepsRequest.getUserId();
        UUID questId = stepsRequest.getQuestId();
        StepsResponseDTO response = questClient.getSteps(userId, questId);
        Integer stepsToComplete = response.getStepsToComplete();
        Integer completedSteps = response.getCompletedSteps();
        Status questStatus = Status.IN_PROGRESS;
        int newStepsValue;

        if (stepsToComplete > (completedSteps + 1)) {
            newStepsValue = completedSteps + 1;
        } else {
            newStepsValue = stepsToComplete;
            questStatus = Status.COMPLETE;

            UpdateBalanceDTO balanceDTO = new UpdateBalanceDTO();
            balanceDTO.setUserId(userId);
            balanceDTO.setNewBalance(questClient.getBalance(userId) + questClient.getCostById(questId));

            questClient.updateBalance(balanceDTO);
        }

        UpdateStepsRequestDTO updateStepsRequest = new UpdateStepsRequestDTO();
        updateStepsRequest.setUserId(userId);
        updateStepsRequest.setQuestId(questId);
        updateStepsRequest.setNewStepsValue(newStepsValue);
        updateStepsRequest.setQuestStatus(questStatus);

        questClient.updateSteps(updateStepsRequest);
    }
}

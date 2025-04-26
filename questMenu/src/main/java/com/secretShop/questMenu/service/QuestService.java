package com.secretShop.questMenu.service;

import com.secretShop.questMenu.DTO.*;
import com.secretShop.questMenu.enums.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QuestService {
    QuestClient questClient = new QuestClient();

    @Transactional
    public void updateSteps(StepsRequestDTO stepsRequest) {
        StepsResponseDTO response = questClient.getSteps(stepsRequest.getUserId(), stepsRequest.getQuestId());

        Status questStatus;

        int newStepsValue;

        Integer stepsToComplete = response.getStepsToComplete();
        Integer completedSteps = response.getCompletedSteps();

        if (stepsToComplete > (completedSteps + 1)){
            newStepsValue = completedSteps + 1;
            questStatus = Status.IN_PROGRESS;
        } else {
            newStepsValue = stepsToComplete;
            questStatus = Status.COMPLETE;

            UpdateBalanceDTO balanceDTO = new UpdateBalanceDTO();
            balanceDTO.setUserId(stepsRequest.getUserId());
            balanceDTO.setCost(questClient.getCostById(stepsRequest.getUserId()));

            questClient.updateBalance(balanceDTO);
        }

        UpdateStepsRequestDTO updateStepsRequest = new UpdateStepsRequestDTO();
        updateStepsRequest.setUserId(stepsRequest.getUserId());
        updateStepsRequest.setQuestId(stepsRequest.getQuestId());
        updateStepsRequest.setNewStepsValue(newStepsValue);
        updateStepsRequest.setQuestStatus(questStatus);

        questClient.updateSteps(updateStepsRequest);
    }
}

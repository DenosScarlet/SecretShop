package com.secretShop.questMenu.service;

import com.secretShop.questMenu.DTO.StepsRequestDTO;
import com.secretShop.questMenu.DTO.StepsResponseDTO;
import com.secretShop.questMenu.DTO.UpdateStepsRequestDTO;
import com.secretShop.questMenu.DTO.UsersQuestDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QuestService {
    QuestClient questClient = new QuestClient();

    @Transactional
    public void updateSteps(StepsRequestDTO stepsRequest) {
        StepsResponseDTO response = questClient.getSteps(stepsRequest.getUserId(), stepsRequest.getQuestId());

        Integer newStepsValue;

        Integer stepsToComplete = response.getStepsToComplete();
        Integer completedSteps = response.getCompletedSteps();

        if (stepsToComplete > (completedSteps + 1)){
            newStepsValue = completedSteps + 1;
        } else {
            newStepsValue = stepsToComplete;
        }

        UpdateStepsRequestDTO updateStepsRequest = new UpdateStepsRequestDTO();
        updateStepsRequest.setUserId(stepsRequest.getUserId());
        updateStepsRequest.setQuestId(stepsRequest.getQuestId());
        updateStepsRequest.setNewStepsValue(newStepsValue);

        questClient.updateSteps(updateStepsRequest);
    }
}

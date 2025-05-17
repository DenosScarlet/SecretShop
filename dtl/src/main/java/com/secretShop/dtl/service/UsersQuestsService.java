package com.secretShop.dtl.service;

import com.secretShop.dtl.DTO.StepsResponseDTO;
import com.secretShop.dtl.DTO.UpdateStepsRequestDTO;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersQuestsService {
    private final UsersQuestsRepository usersQuestsRepository;
    private final QuestRepository questRepository;

    public StepsResponseDTO getSteps(UUID userId, UUID questId) {
        StepsResponseDTO response = new StepsResponseDTO();
        response.setStepsToComplete(questRepository.getStepsToCompleteById(questId));
        response.setCompletedSteps(usersQuestsRepository.getCompletedStepsById(userId));
        return response;
    }

    @Transactional
    public void updateSteps(UpdateStepsRequestDTO request) {
        usersQuestsRepository.updateCompletedStepsById(request.getUserId(), request.getQuestId(), request.getNewStepsValue());
        usersQuestsRepository.updateQuestStatusById(request.getUserId(), request.getQuestId(), request.getQuestStatus());
    }
}

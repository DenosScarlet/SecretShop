package com.secretShop.dtl.service;

import com.secretShop.dtl.DTO.QuestStatusDTO;
import com.secretShop.dtl.DTO.StepsResponseDTO;
import com.secretShop.dtl.DTO.UpdateStepsRequestDTO;
import com.secretShop.dtl.entity.UsersQuests;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<QuestStatusDTO> getUserQuests(UUID userId) {
        List<UsersQuests> userQuests = usersQuestsRepository.findByUser_UserIdWithQuest(userId);
        return userQuests.stream()
                .map(uq -> new QuestStatusDTO(
                        uq.getQuest().getQuestId(),
                        uq.getQuestStatus(),
                        uq.getCompletedSteps()
                ))
                .collect(Collectors.toList());
    }
}

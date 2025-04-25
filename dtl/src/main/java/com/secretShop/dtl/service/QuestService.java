package com.secretShop.dtl.service;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.convertor.QuestMapper;
import com.secretShop.dtl.service.dto.QuestDTO;
import com.secretShop.dtl.service.dto.StepsRequestDTO;
import com.secretShop.dtl.service.dto.StepsResponseDTO;
import com.secretShop.dtl.service.dto.UpdateStepsRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final UsersQuestsRepository usersQuestsRepository;
    private final QuestMapper questMapper;

    @Transactional
    public QuestDTO createQuestWithUserRelations(QuestDTO request) {
        Quest newQuest = new Quest();
        newQuest.setQuest_title(request.getQuestTitle());
        newQuest.setDescription(request.getDescription());
        newQuest.setSteps_to_complete(request.getStepsToComplete());
        newQuest.setFrequency(request.getFrequency());
        newQuest.setWork_group(request.getWorkGroup());
        newQuest.setStart_date(request.getStartDate());
        newQuest.setEnd_date(request.getEndDate());
        newQuest.setCost(request.getCost());
        Quest savedQuest = questRepository.save(newQuest);

        List<User> users = userRepository.findUsersByWorkGroup(request.getWorkGroup());

        users.forEach(user -> {
            usersQuestsRepository.createUserQuestRelation(user.getUser_id(), savedQuest.getQuest_id());

            // ИЛИ через сохранение сущности (если нужна валидация):
            /*
            UsersQuests relation = new UsersQuests();
            relation.setUser(user);
            relation.setQuest(savedQuest);
            usersQuestsRepository.save(relation);
            */
        });

        return questMapper.modelToDto(savedQuest);
    }

    public StepsResponseDTO getSteps(UUID userId, UUID questId){
        StepsResponseDTO response = new StepsResponseDTO();
        response.setStepsToComplete(questRepository.getStepsToCompleteById(questId));
        response.setCompletedSteps(usersQuestsRepository.getCompletedStepsById(userId));
        return response;
    }

    @Transactional
    public void updateSteps(UpdateStepsRequestDTO request){
        usersQuestsRepository.updateCompletedStepsById(request.getUserId(), request.getQuestId(), request.getNewStepsValue());
    }
}
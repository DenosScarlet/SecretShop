package com.secretShop.dtl.service;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.convertor.QuestMapper;
import com.secretShop.dtl.DTO.*;
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
        newQuest.setQuestTitle(request.getQuestTitle());
        newQuest.setDescription(request.getDescription());
        newQuest.setStepsToComplete(request.getStepsToComplete());
        newQuest.setFrequency(request.getFrequency());
        newQuest.setWorkGroup(request.getWorkGroup());
        newQuest.setStartDate(request.getStartDate());
        newQuest.setEndDate(request.getEndDate());
        newQuest.setCost(request.getCost());
        Quest savedQuest = questRepository.save(newQuest);

        List<User> users = userRepository.findUsersByWorkGroup(request.getWorkGroup());

        users.forEach(user -> {
            usersQuestsRepository.createUserQuestRelation(user.getUserId(), savedQuest.getQuestId());
        });

        return questMapper.modelToDto(savedQuest);
    }
}
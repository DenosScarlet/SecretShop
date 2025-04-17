package com.secretShop.dtl.service;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.convertor.QuestMapper;
import com.secretShop.dtl.service.dto.QuestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final UsersQuestsRepository usersQuestsRepository;
    private final QuestMapper questMapper;

    @Transactional
    public QuestDTO createQuestWithUserRelations(QuestDTO request) {
        // 1. Создаём квест
        Quest newQuest = new Quest();
        newQuest.setQuest_title(request.getQuest_title());
        newQuest.setDescription(request.getDescription());
        newQuest.setSteps_to_complete(request.getSteps_to_complete());
        newQuest.setFrequency(request.getFrequency());
        newQuest.setWork_group(request.getWork_group());
        newQuest.setStart_date(request.getStart_date());
        newQuest.setEnd_date(request.getEnd_date());
        newQuest.setCost(request.getCost());
        Quest savedQuest = questRepository.save(newQuest);

        // 2. Ищем пользователей с той же группой
        List<User> users = userRepository.findUsersByWorkGroup(request.getWork_group());

        // 3. Создаём связи
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
}
package com.secretShop.dtl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.enums.WorkGroup;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.convertor.QuestMapper;
import com.secretShop.dtl.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;
    private final UsersQuestsRepository usersQuestsRepository;
    private final QuestMapper questMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public QuestDTO createQuestWithUserRelations(QuestRequest questRequest) {
        Object payload = questRequest.getPayload();
        QuestDTO questDTO = objectMapper.convertValue(payload, QuestDTO.class);

        Quest newQuest = new Quest();
        newQuest.setQuestTitle(questDTO.getQuestTitle());
        newQuest.setDescription(questDTO.getDescription());
        newQuest.setStepsToComplete(questDTO.getStepsToComplete());
        newQuest.setFrequency(questDTO.getFrequency());
        newQuest.setWorkGroup(questDTO.getWorkGroup());
        newQuest.setStartDate(questDTO.getStartDate());
        newQuest.setEndDate(questDTO.getEndDate());
        newQuest.setCost(questDTO.getCost());

        Quest savedQuest = questRepository.save(newQuest);

        List<User> users = userRepository.findUsersByWorkGroup(questDTO.getWorkGroup());

        users.forEach(user -> {
            usersQuestsRepository.createUserQuestRelation(user.getUserId(), savedQuest.getQuestId());
        });

        return questMapper.modelToDto(savedQuest);
    }

    @Transactional
    public QuestDTO updateQuest(UpdateQuestDTO updateQuestDTO) {
        UUID questId = updateQuestDTO.getQuestId();
        QuestDTO questDTO = updateQuestDTO.getQuestDTO();
        // Получаем существующий квест
        Quest existingQuest = questRepository.findById(questId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quest not found with id: " + questId
                ));

        // Обновляем поля через маппер
        questMapper.updateFromDto(questDTO, existingQuest);

        // Сохраняем обновлённый квест
        Quest updatedQuest = questRepository.save(existingQuest);

        // Обновляем связи с пользователями
        updateUserRelations(updatedQuest, questDTO.getWorkGroup());

        return questMapper.modelToDto(updatedQuest);
    }

    private void updateUserRelations(Quest quest, WorkGroup newWorkGroup) {
        // Получаем текущих связанных пользователей
        List<UUID> currentUserIds = usersQuestsRepository.findUserIdsByQuestId(quest.getQuestId());

        // Получаем новых пользователей по рабочей группе
        List<User> newUsers = userRepository.findUsersByWorkGroup(newWorkGroup);
        List<UUID> newUserIds = newUsers.stream()
                .map(User::getUserId)
                .toList();

        // Удаляем устаревшие связи
        currentUserIds.stream()
                .filter(userId -> !newUserIds.contains(userId))
                .forEach(userId ->
                        usersQuestsRepository.deleteByUserIdAndQuestId(userId, quest.getQuestId())
                );

        // Добавляем новые связи
        newUserIds.stream()
                .filter(userId -> !currentUserIds.contains(userId))
                .forEach(userId ->
                        usersQuestsRepository.createUserQuestRelation(userId, quest.getQuestId())
                );
    }
}
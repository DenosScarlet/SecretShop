package com.secretShop.questMenu.service;

import com.secretShop.questMenu.DTO.QuestRequest;
import com.secretShop.questMenu.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaQuestClient {
    private final RequestReplyProducer producer;

    public List<QuestDTO> findAllQuests() {
        return producer.sendAndReceive(new QuestRequest("FIND_ALL", null, null), List.class).join();
    }

    public QuestDTO findQuestById(UUID id) {
        return producer.sendAndReceive(new QuestRequest("FIND_BY_ID", id.toString(), null), QuestDTO.class).join();
    }

    public Integer getCostById(UUID id) {
        return producer.sendAndReceive(new QuestRequest("COST_BY_ID", id.toString(), null), Integer.class).join();
    }

    public String getQuestStatusById(UUID userId, UUID questId) {
        StepsRequestDTO dto = new StepsRequestDTO(userId, questId);
        return producer.sendAndReceive(new QuestRequest("STATUS_BY_ID", dto, null), String.class).join();
    }

    public List<UsersQuestsDTO> getUserQuests(UUID userId) {
        return producer.sendAndReceive(
                new QuestRequest("GET_USERS_QUESTS", userId.toString(), null),
                List.class
        ).join();
    }

    public StepsResponseDTO getSteps(UUID userId, UUID questId) {
        StepsRequestDTO dto = new StepsRequestDTO(userId, questId);
        return producer.sendAndReceive(new QuestRequest("GET_STEPS", dto, null), StepsResponseDTO.class).join();
    }

    public void updateSteps(UpdateStepsRequestDTO dto) {
        producer.sendAndReceive(new QuestRequest("UPDATE_STEPS", dto, null), Object.class);
    }

    public Integer getBalance(UUID userId) {
        return producer.sendAndReceive(new QuestRequest("GET_BALANCE", userId.toString(), null), Integer.class).join();
    }

    public void updateBalance(UpdateBalanceDTO dto) {
        producer.sendAndReceive(new QuestRequest("UPDATE_BALANCE", dto, null), Object.class);
    }

    public QuestDTO saveQuest(QuestDTO quest) {
        return producer.sendAndReceive(new QuestRequest("SAVE_QUEST", quest, null), QuestDTO.class).join();
    }

    public QuestDTO updateQuest(UUID questId, QuestDTO quest) {
        UpdateQuestDTO updateQuestDTO = new UpdateQuestDTO(questId, quest);
        return producer.sendAndReceive(new QuestRequest("UPDATE_QUEST", updateQuestDTO, null), QuestDTO.class).join();
    }

    public void deleteQuest(UUID questId) {
        producer.sendAndReceive(new QuestRequest("DELETE_QUEST", questId.toString(), null), QuestDTO.class).join();
    }
}
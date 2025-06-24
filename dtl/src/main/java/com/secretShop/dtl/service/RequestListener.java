package com.secretShop.dtl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretShop.dtl.DTO.*;
import com.secretShop.dtl.DTO.QuestRequest;
import com.secretShop.dtl.controller.UsersQuestsController;
import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.convertor.QuestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestListener {
    private final KafkaTemplate<String, QuestRequest> kafkaTemplate;
    private final QuestService questService;
    private final UsersQuestsService usersQuestsService;
    private final UserService userService;
    private final QuestRepository questRepository;
    private final UsersQuestsRepository usersQuestsRepository;
    private final UserRepository userRepository;

    private final QuestMapper questMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "quest-requests", containerFactory = "questKafkaListenerContainerFactory")
    public void listen(QuestRequest request) {
        String op = request.getOperation();
        Object payload = request.getPayload();
        String correlationId = request.getCorrelationId();
        Object result = null;

        switch (op) {
            case "FIND_ALL": {
                result = questRepository.findAll();
                break;
            }
            case "FIND_BY_ID": {
                result = questRepository.findById(UUID.fromString((String) payload)).map(questMapper::modelToDto);
                break;
            }
            case "COST_BY_ID": {
                result = questRepository.getCostById(UUID.fromString((String) payload));
                break;
            }
            case "STATUS_BY_ID": {
                StepsRequestDTO srd = objectMapper.convertValue(payload, StepsRequestDTO.class);
                result = usersQuestsRepository.getQuestStatusById(srd.getUserId(), srd.getQuestId());
                break;
            }
            case "GET_STEPS": {
                StepsRequestDTO sr = objectMapper.convertValue(payload, StepsRequestDTO.class);
                result = usersQuestsService.getSteps(sr.getUserId(), sr.getQuestId());
                break;
            }
            case "UPDATE_STEPS": {
                UpdateStepsRequestDTO us = objectMapper.convertValue(payload, UpdateStepsRequestDTO.class);
                usersQuestsService.updateSteps(us);
                break;
            }
            case "GET_BALANCE": {
                result = userRepository.getBalanceById(UUID.fromString((String) payload));
                break;
            }
            case "UPDATE_BALANCE": {
                UpdateBalanceDTO ub = objectMapper.convertValue(payload, UpdateBalanceDTO.class);
                userService.updateBalance(ub);
                break;
            }
            case "SAVE_QUEST": {
                result = questService.createQuestWithUserRelations(request);
                break;
            }
            case "UPDATE_QUEST": {
                UpdateQuestDTO updateQuestDTO = objectMapper.convertValue(payload, UpdateQuestDTO.class);
                result = questService.updateQuest(updateQuestDTO);
                break;
            }
            case "DELETE_QUEST": {
                questRepository.findById(UUID.fromString((String) payload)).ifPresent(questRepository::delete);
                break;
            }
            case "GET_USERS_QUESTS": {
                UUID userId = UUID.fromString((String) payload);
                result = usersQuestsService.getUserQuests(userId);
                break;
            }
        }
        kafkaTemplate.send("quest-responses", new QuestRequest(op, result, correlationId));
    }
}
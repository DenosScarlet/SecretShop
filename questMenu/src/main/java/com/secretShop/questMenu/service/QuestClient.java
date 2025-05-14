package com.secretShop.questMenu.service;

import com.secretShop.questMenu.DTO.QuestDTO;
import com.secretShop.questMenu.DTO.StepsResponseDTO;
import com.secretShop.questMenu.DTO.UpdateBalanceDTO;
import com.secretShop.questMenu.DTO.UpdateStepsRequestDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

public class QuestClient {
    RestClient restClient;

    /// DTL Quests API

    public QuestClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8580")
                .build();
    }

    public List<QuestDTO> findAllQuests() {
        return restClient.get()
                .uri("/api/quest")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public QuestDTO findQuestById(UUID id) {
        return restClient.get()
                .uri("/api/quest/{id}", id)
                .retrieve()
                .body(QuestDTO.class);
    }

    public Integer getCostById(UUID questId) {
        return restClient.get()
                .uri("/api/quest/cost/{id}", questId)
                .retrieve()
                .body(Integer.class);
    }

    public String getQuestStatusById(UUID questId) {
        return restClient.get()
                .uri("/api/quest/usersQuest/questStatus/{id}", questId)
                .retrieve()
                .body(String.class);
    }

    public QuestDTO saveQuest(QuestDTO quest) {
        return restClient.post()
                .uri("/api/quest")
                .contentType(MediaType.APPLICATION_JSON)
                .body(quest)
                .retrieve()
                .body(QuestDTO.class);
    }

    public void deleteQuest(UUID id) {
        restClient.delete()
                .uri("/api/quest/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    /// DTL UsersQuests API

    public StepsResponseDTO getSteps(UUID userId, UUID questId) {
        return restClient.get()
                .uri("/api/users_quests/steps/{userId}&{questId}", userId, questId)
                .retrieve()
                .body(StepsResponseDTO.class);
    }

    public void updateSteps(UpdateStepsRequestDTO request) {
        restClient.patch()
                .uri("/api/users_quests/steps/update")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    /// DTL User API

    public Integer getBalance(UUID userId) {
        return restClient.get()
                .uri("/api/user/balance/{userId}", userId)
                .retrieve()
                .body(Integer.class);
    }

    public void updateBalance(UpdateBalanceDTO balanceDTO) {
        restClient.patch()
                .uri("/api/user/balance/update")
                .contentType(MediaType.APPLICATION_JSON)
                .body(balanceDTO)
                .retrieve()
                .toBodilessEntity();
    }
}

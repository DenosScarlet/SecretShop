package com.secretShop.questMenu.service;

import com.secretShop.questMenu.DTO.QuestDTO;
import com.secretShop.questMenu.DTO.StepsRequestDTO;
import com.secretShop.questMenu.DTO.StepsResponseDTO;
import com.secretShop.questMenu.DTO.UpdateStepsRequestDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

public class QuestClient {
    RestClient restClient;

    public QuestClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8580")
                .build();
    }


    public List<QuestDTO> findAll() {
        return restClient.get()
                .uri("/api/quest")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public QuestDTO findById(UUID id) {
        return restClient.get()
                .uri("/api/quest/{id}", id)
                .retrieve()
                .body(QuestDTO.class);
    }

    public QuestDTO save(QuestDTO quest) {
        return restClient.post()
                .uri("/api/quest")
                .contentType(MediaType.APPLICATION_JSON)
                .body(quest)
                .retrieve()
                .body(QuestDTO.class);
    }

    public void delete(UUID id) {
        restClient.delete()
                .uri("/api/quest/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    public StepsResponseDTO getSteps(UUID userId, UUID questId) {
        return restClient.get()
                .uri("/api/quest/steps/{userId}&{questId}", userId, questId)
                .retrieve()
                .body(StepsResponseDTO.class);
    }

    public ResponseEntity<Void> updateSteps(UpdateStepsRequestDTO request){
        return restClient.patch()
                .uri("/api/quest/steps/update")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}

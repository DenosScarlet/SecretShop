package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.QuestStatusDTO;
import com.secretShop.dtl.DTO.StepsResponseDTO;
import com.secretShop.dtl.DTO.UpdateStepsRequestDTO;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.UsersQuestsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users_quests")
@RequiredArgsConstructor
public class UsersQuestsController {
    private final UsersQuestsService usersQuestsService;
    private final UsersQuestsRepository usersQuestsRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuestStatusDTO>> getUsersQuests(@PathVariable UUID userId) {
        List<QuestStatusDTO> userQuests = usersQuestsService.getUserQuests(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userQuests);
    }

    @GetMapping("/quest_status/{id}")
    public String getQuestStatusById(@PathVariable("id") UUID questId) {
        return usersQuestsRepository.getQuestStatusById(questId);
    }

    @GetMapping("/steps/{userId}&{questId}")
    public ResponseEntity<StepsResponseDTO> getSteps(
            @PathVariable("userId") UUID userId, @PathVariable("questId") UUID questId) {
        StepsResponseDTO response = usersQuestsService.getSteps(userId, questId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/steps/update")
    public void updateSteps(@RequestBody UpdateStepsRequestDTO request) {
        usersQuestsService.updateSteps(request);
    }
}
 
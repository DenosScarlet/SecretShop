package com.secretShop.dtl.controller;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.service.QuestService;
import com.secretShop.dtl.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quest")
@RequiredArgsConstructor
public class QuestController {
    private final QuestRepository questRepository;
    private final QuestService questService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Quest> getAllQuests() {
        return questRepository.findAll();
    }

    @GetMapping("/{id}")
    public Quest getQuestById(@PathVariable("id") UUID id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Квест не найден."));
    }

    @GetMapping("/cost/{id}")
    public Integer getCostById(@PathVariable("id") UUID questId) {
        return questRepository.getCostById(questId);
    }

//    @PostMapping
//    public Quest createQuest(@RequestBody Quest quest) {
//        return questRepository.save(quest);
//    }


    @PutMapping("/{id}")
    public Quest updateQuest(@PathVariable("id") UUID id, @RequestBody Quest quest) {
        if (!questRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сущность с id `%s` не найдена".formatted(id));
        }
        return questRepository.save(quest);
    }

    @DeleteMapping("/{id}")
    public Quest deleteQuest(@PathVariable("id") UUID id) {
        Quest quest = questRepository.findById(id).orElse(null);
        if (quest != null) {
            questRepository.delete(quest);
        }
        return quest;
    }

    @PostMapping
    public ResponseEntity<QuestDTO> createQuest(
            @RequestBody @Validated QuestDTO request) {
        QuestDTO createdQuest = questService.createQuestWithUserRelations(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuest);
    }

    @GetMapping("/steps/{userId}&{questId}")
    public ResponseEntity<StepsResponseDTO> getSteps(
            @PathVariable("userId") UUID userId, @PathVariable("questId") UUID questId) {
        StepsResponseDTO response = questService.getSteps(userId, questId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/steps/update")
    public void updateSteps(@RequestBody UpdateStepsRequestDTO request) {
        questService.updateSteps(request);
    }

    @GetMapping("/balance/{userId}")
    public Integer getBalance(@PathVariable("userId") UUID userId) {
        return userRepository.getBalanceById(userId);
    }

    @PatchMapping("/balance/update")
    public void updateBalance(@RequestBody UpdateBalanceDTO balanceDTO) {
        questService.updateBalance(balanceDTO);
    }


    @GetMapping("/user")
    public PagedModel<User> getAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return new PagedModel<>(users);
    }
}

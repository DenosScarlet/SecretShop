package com.secretShop.dtl.controller;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.QuestService;
import com.secretShop.dtl.DTO.*;
import lombok.RequiredArgsConstructor;
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
    private final UsersQuestsRepository usersQuestsRepository;

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

    @PostMapping
    public ResponseEntity<QuestDTO> createQuest(@RequestBody @Validated QuestDTO request) {
        QuestDTO createdQuest = questService.createQuestWithUserRelations(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuest);
    }

    @PutMapping("/{id}")
    public QuestDTO updateQuest(@PathVariable("id") UUID id, @RequestBody QuestDTO questDTO) {
        if (!questRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сущность с id `%s` не найдена".formatted(id));
        }
        return questService.updateQuest(id, questDTO);
    }

    @DeleteMapping("/{id}")
    public Quest deleteQuest(@PathVariable("id") UUID id) {
        Quest quest = questRepository.findById(id).orElse(null);
        if (quest != null) {
            questRepository.delete(quest);
        }
        return quest;
    }
}

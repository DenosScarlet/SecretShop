package com.secretShop.dtl.controller;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quest")
@RequiredArgsConstructor
public class QuestController {
    private final QuestRepository questRepository;

    @GetMapping
    public List<Quest> getAllQuests() {
        return questRepository.findAll();
    }

    @GetMapping("/{id}")
    public Quest getQuestById(@PathVariable("id") UUID id){
        return questRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Квест не найден."));
    }
    @PostMapping
    public Quest createQuest(@RequestBody Quest quest){
        return questRepository.save(quest);
    }


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

}

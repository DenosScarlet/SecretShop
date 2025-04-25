package com.secretShop.questMenu.controller;

import com.secretShop.questMenu.DTO.QuestDTO;
import com.secretShop.questMenu.DTO.StepsRequestDTO;
import com.secretShop.questMenu.service.QuestClient;
import com.secretShop.questMenu.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quest")
@RequiredArgsConstructor
public class QuestMenuController {

    private final QuestClient questClient = new QuestClient();
    private final QuestService questService = new QuestService();

    @GetMapping
    public List<QuestDTO> getAllQuests() {
        return questClient.findAll();
    }

    @GetMapping("/{id}")
    public QuestDTO getQuestById(@PathVariable("id") UUID id) {
        return questClient.findById(id);
    }

    @PostMapping
    public QuestDTO createQuest(@RequestBody QuestDTO quest) {
        return questClient.save(quest);
    }

    @PutMapping("/{id}")
    public QuestDTO updateQuest(@PathVariable("id") UUID id, @RequestBody QuestDTO quest) {
        return questClient.save(quest);
    }

    @DeleteMapping("/{id}")
    public void deleteQuest(@PathVariable("id") UUID id) {
        questClient.delete(id);
    }
    @PatchMapping("/steps/update")
    public void updateSteps(@RequestBody StepsRequestDTO stepsRequest){
        questService.updateSteps(stepsRequest);
    }
}

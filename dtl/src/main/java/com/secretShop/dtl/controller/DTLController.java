package com.secretShop.dtl.controller;

import com.secretShop.dtl.service.QuestService;
import com.secretShop.dtl.service.dto.QuestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class DTLController {
    private final QuestService questService;

    @GetMapping("/quests")
    public List<QuestDTO> allQuests() {
        return questService.findAll();
    }

    @GetMapping("/quest/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<QuestDTO> getQuest(@PathVariable UUID id) {
        return ResponseEntity.ok().body(questService.findById(id));
    }

    @PostMapping("/quest")
    public ResponseEntity<QuestDTO> createQuest( @RequestBody QuestDTO quest) throws URISyntaxException {
        QuestDTO result = questService.save(quest);
        return ResponseEntity.created(new URI("/api/v1/quests/" + result.getQuest_id()))
                .body(result);
    }

    @PutMapping("/quest/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<QuestDTO> updateQuest( @PathVariable UUID id, @RequestBody QuestDTO quest) {
        return ResponseEntity.ok().body(questService.save(quest));
    }

    @DeleteMapping("/quest/{id}")
    public ResponseEntity<?> deleteQuest(@PathVariable UUID id) {
        questService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

package com.secretShop.dtl.controller;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.QuestService;
import com.secretShop.dtl.DTO.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Tag(name = "Управление квестами", description = "API для работы квестами")
@RestController
@RequestMapping("/api/quest")
@RequiredArgsConstructor
public class QuestController {
    private final QuestRepository questRepository;
    private final QuestService questService;

    @Operation(summary = "Получить все квесты", description = "Возвращает список всех доступных квестов")
    @ApiResponse(responseCode = "200", description = "Квесты успешно получены")
    @GetMapping
    public List<Quest> getAllQuests() {
        return questRepository.findAll();
    }

    @Operation(summary = "Получить квест по ID", description = "Возвращает данные конкретного квеста по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квест найден"),
            @ApiResponse(responseCode = "404", description = "Квест не найден")
    })
    @GetMapping("/{id}")
    public Quest getQuestById(
            @Parameter(description = "ID квеста", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Квест не найден."));
    }

    @Operation(summary = "Получить стоимость квеста", description = "Возвращает стоимость конкретного квеста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Стоимость получена"),
            @ApiResponse(responseCode = "404", description = "Квест не найден")
    })
    @GetMapping("/cost/{id}")
    public Integer getCostById(
            @Parameter(description = "ID квеста", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID questId) {
        return questRepository.getCostById(questId);
    }

    @Operation(summary = "Создать квест", description = "Добавляет новый квест в систему и устанавливает связи с сотрудниками")
    @ApiResponse(responseCode = "201", description = "Квест успешно создан")
    @PostMapping
    public ResponseEntity<QuestDTO> createQuest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания квеста", required = true,
                    content = @Content(schema = @Schema(implementation = QuestDTO.class)))
            @RequestBody @Validated QuestDTO request) {
        QuestDTO createdQuest = questService.createQuestWithUserRelations(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuest);
    }

    @Operation(summary = "Обновить квест", description = "Изменяет данные существующего квеста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квест обновлен"),
            @ApiResponse(responseCode = "404", description = "Квест не найден")
    })
    @PutMapping("/{id}")
    public QuestDTO updateQuest(
            @Parameter(description = "ID квеста", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные квеста", required = true,
                    content = @Content(schema = @Schema(implementation = QuestDTO.class)))
            @RequestBody QuestDTO questDTO) {
        if (!questRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сущность с id `%s` не найдена".formatted(id));
        }
        return questService.updateQuest(id, questDTO);
    }

    @Operation(summary = "Удалить квест", description = "Удаляет квест из системы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квест удален"),
            @ApiResponse(responseCode = "404", description = "Квест не найден")
    })
    @DeleteMapping("/{id}")
    public Quest deleteQuest(
            @Parameter(description = "ID квеста", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        Quest quest = questRepository.findById(id).orElse(null);
        if (quest != null) {
            questRepository.delete(quest);
        }
        return quest;
    }
}
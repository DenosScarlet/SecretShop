package com.secretShop.questMenu.controller;

import com.secretShop.questMenu.DTO.QuestDTO;
import com.secretShop.questMenu.DTO.StepsRequestDTO;
import com.secretShop.questMenu.service.KafkaQuestClient;
import com.secretShop.questMenu.service.QuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/quest")
@RequiredArgsConstructor
@Tag(name = "Управление квестами", description = "API для управления квестами")
public class QuestMenuController {

    private final KafkaQuestClient kafkaQuestClient;
    private final QuestService questService;

    @Operation(summary = "Получить все квесты", description = "Возвращает список всех доступных квестов")
    @ApiResponse(responseCode = "200", description = "Квесты успешно получены",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = QuestDTO.class))))
    @GetMapping
    public List<QuestDTO> getAllQuests() {
        return kafkaQuestClient.findAllQuests();
    }

    @Operation(summary = "Получить квест по ID", description = "Возвращает конкретный квест по его уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квест найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QuestDTO.class))),
            @ApiResponse(responseCode = "404", description = "Квест не найден")
    })
    @GetMapping("/{id}")
    public QuestDTO getQuestById(
            @Parameter(description = "Уникальный идентификатор квеста", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        return kafkaQuestClient.findQuestById(id);
    }

    @Operation(summary = "Создать новый квест", description = "Добавляет новый квест в систему")
    @ApiResponse(responseCode = "200", description = "Квест успешно создан",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = QuestDTO.class)))
    @PostMapping
    public QuestDTO createQuest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Детали квеста для создания", required = true,
                    content = @Content(schema = @Schema(implementation = QuestDTO.class)))
            @RequestBody QuestDTO quest) {
        log.info("Received quest creation request: {}", quest);
        return kafkaQuestClient.saveQuest(quest);
    }

    @Operation(summary = "Обновить квест", description = "Редактирует существующий квест по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квест обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = QuestDTO.class))),
            @ApiResponse(responseCode = "404", description = "Квест не найден")
    })
    @PutMapping("/{id}")
    public QuestDTO updateQuest(
            @Parameter(description = "Уникальный идентификатор квеста", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные квеста", required = true,
                    content = @Content(schema = @Schema(implementation = QuestDTO.class)))
            @RequestBody QuestDTO quest) {
        return kafkaQuestClient.updateQuest(id, quest);
    }

    @Operation(summary = "Удалить квест", description = "Удаляет квест из системы по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Квест удален"),
            @ApiResponse(responseCode = "404", description = "Квест не найден")
    })
    @DeleteMapping("/{id}")
    public void deleteQuest(
            @Parameter(description = "Уникальный идентификатор квеста", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        kafkaQuestClient.deleteQuest(id);
    }

    @Operation(summary = "Обновить шаги квеста", description = "Изменяет шаги для существующего квеста")
    @ApiResponse(responseCode = "200", description = "Шаги успешно обновлены")
    @PatchMapping("/steps/update")
    public void updateSteps(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления шагов", required = true,
                    content = @Content(schema = @Schema(implementation = StepsRequestDTO.class)))
            @RequestBody StepsRequestDTO stepsRequest) {
        questService.updateSteps(stepsRequest);
    }
}
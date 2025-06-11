package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.QuestStatusDTO;
import com.secretShop.dtl.DTO.StepsResponseDTO;
import com.secretShop.dtl.DTO.UpdateStepsRequestDTO;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.UsersQuestsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Управление квестами сотрудников", description = "API для работы с квестами сотрудников")
@RestController
@RequestMapping("/api/users_quests")
@RequiredArgsConstructor
public class UsersQuestsController {
    private final UsersQuestsService usersQuestsService;
    private final UsersQuestsRepository usersQuestsRepository;

    @Operation(summary = "Получить квесты сотрудника", description = "Возвращает все квесты определенного сотрудника")
    @ApiResponse(responseCode = "200", description = "Квесты успешно получены")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuestStatusDTO>> getUsersQuests(
            @Parameter(description = "ID сотрудника", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId) {
        List<QuestStatusDTO> userQuests = usersQuestsService.getUserQuests(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userQuests);
    }

//    @Operation(summary = "Получить статус квеста", description = "Возвращает текущий статус квеста")
//    @ApiResponse(responseCode = "200", description = "Статус получен")
//    @GetMapping("/quest_status/{id}")
//    public String getQuestStatusById(
//            @Parameter(description = "ID квеста", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
//            @PathVariable("id") UUID questId) {
//        return usersQuestsRepository.getQuestStatusById(questId);
//    }

    @Operation(summary = "Получить прогресс выполнения", description = "Возвращает количество выполненных шагов квеста")
    @ApiResponse(responseCode = "200", description = "Прогресс получен")
    @GetMapping("/steps/{userId}&{questId}")
    public ResponseEntity<StepsResponseDTO> getSteps(
            @Parameter(description = "ID сотрудника", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("userId") UUID userId,
            @Parameter(description = "ID квеста", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("questId") UUID questId) {
        StepsResponseDTO response = usersQuestsService.getSteps(userId, questId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Обновить прогресс квеста", description = "Обновляет количество выполненных шагов для квеста")
    @ApiResponse(responseCode = "200", description = "Прогресс обновлен")
    @PatchMapping("/steps/update")
    public void updateSteps(
            @RequestBody UpdateStepsRequestDTO request) {
        usersQuestsService.updateSteps(request);
    }
}
 
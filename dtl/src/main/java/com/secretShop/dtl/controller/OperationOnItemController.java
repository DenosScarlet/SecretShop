package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.CreateOperationOnItemDTO;
import com.secretShop.dtl.DTO.OperationOnItemDTO;
import com.secretShop.dtl.DTO.UpdateOperationDTO;
import com.secretShop.dtl.service.OperationOnItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Операции с товарами", description = "API для управления операциями над товарами")
@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationOnItemController {
    private final OperationOnItemService service;

    @Operation(summary = "Создать операцию", description = "Добавляет новую операцию над товаром")
    @ApiResponse(responseCode = "201", description = "Операция создана")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperationOnItemDTO createOperation(
            @RequestBody CreateOperationOnItemDTO dto) {
        return service.createOperation(dto);
    }

    @Operation(summary = "Получить операцию", description = "Возвращает данные конкретной операции")
    @ApiResponse(responseCode = "200", description = "Операция найдена")
    @GetMapping("/{id}")
    public OperationOnItemDTO getOperation(
            @Parameter(description = "ID операции", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        return service.getOperationById(id);
    }

    @Operation(summary = "Получить все операции", description = "Возвращает список всех операций")
    @ApiResponse(responseCode = "200", description = "Операции получены")
    @GetMapping
    public List<OperationOnItemDTO> getAllOperations() {
        return service.getAllOperations();
    }

    @Operation(summary = "Получить операции пользователя", description = "Возвращает операции определенного сотрудника")
    @ApiResponse(responseCode = "200", description = "Операции получены")
    @GetMapping("/user/{userId}")
    public List<OperationOnItemDTO> getOperationsByUser(
            @Parameter(description = "ID сотрудника", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("userId") UUID userId) {
        return service.getOperationsByUserId(userId);
    }

    @Operation(summary = "Получить операции по товару", description = "Возвращает операции с определенным товаром")
    @ApiResponse(responseCode = "200", description = "Операции получены")
    @GetMapping("/item/{itemId}")
    public List<OperationOnItemDTO> getOperationsByItem(
            @Parameter(description = "ID товара", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("itemId") UUID itemId) {
        return service.getOperationsByItemId(itemId);
    }

    @Operation(summary = "Обновить операцию", description = "Изменяет данные операции")
    @ApiResponse(responseCode = "200", description = "Операция обновлена")
    @PutMapping("/{id}")
    public OperationOnItemDTO updateOperation(
            @Parameter(description = "ID операции", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id,
            @RequestBody UpdateOperationDTO dto) {
        return service.updateOperation(id, dto);
    }

    @Operation(summary = "Удалить операцию", description = "Удаляет операцию из системы")
    @ApiResponse(responseCode = "204", description = "Операция удалена")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOperation(
            @Parameter(description = "ID операции", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        service.deleteOperation(id);
    }
}
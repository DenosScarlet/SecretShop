package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.UpdateBalanceDTO;
import com.secretShop.dtl.DTO.UserDTO;
import com.secretShop.dtl.enums.WorkGroup;
import com.secretShop.dtl.service.interfaces.UserService;
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

@Tag(name = "Управление сотрудниками", description = "API для работы с сотрудниками")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Получить всех сотрудников", description = "Возвращает список всех сотрудников")
    @ApiResponse(responseCode = "200", description = "Сотрудники получены")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Получить сотрудника по ID", description = "Возвращает данные конкретного сотрудника")
    @ApiResponse(responseCode = "200", description = "Сотрудник найден")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID сотрудника", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Создать сотрудника", description = "Добавляет нового сотрудника в систему")
    @ApiResponse(responseCode = "201", description = "Сотрудник создан")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(userDTO));
    }

    @Operation(summary = "Обновить сотрудника", description = "Изменяет данные существующего сотрудника")
    @ApiResponse(responseCode = "200", description = "Сотрудник обновлен")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID сотрудника", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id,
            @RequestBody UserDTO userDTO) {
        userDTO.setUserId(id);
        return ResponseEntity.ok(userService.save(userDTO));
    }

    @Operation(summary = "Удалить сотрудника", description = "Удаляет сотрудника из системы")
    @ApiResponse(responseCode = "204", description = "Сотрудник удален")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID сотрудника", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновить баланс", description = "Изменяет баланс сотрудника")
    @ApiResponse(responseCode = "204", description = "Баланс обновлен")
    @PostMapping("/balance")
    public ResponseEntity<Void> updateUserBalance(
            @RequestBody UpdateBalanceDTO balanceDTO) {
        userService.updateBalance(balanceDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить рабочие группы", description = "Возвращает список всех доступных рабочих групп")
    @ApiResponse(responseCode = "200", description = "Группы получены")
    @GetMapping("/work-groups")
    public ResponseEntity<WorkGroup[]> getAvailableWorkGroups() {
        return ResponseEntity.ok(WorkGroup.values());
    }

    @Operation(summary = "Получить сотрудников по группе", description = "Возвращает сотрудников определенной рабочей группы")
    @ApiResponse(responseCode = "200", description = "Сотрудники получены")
    @GetMapping("/work-group/{workGroup}")
    public ResponseEntity<List<UserDTO>> getUsersByWorkGroup(
            @Parameter(description = "Название рабочей группы", required = true, example = "DEVELOPMENT")
            @PathVariable("workGroup") String workGroup) {
        return ResponseEntity.ok(userService.findByWorkGroup(workGroup));
    }
}
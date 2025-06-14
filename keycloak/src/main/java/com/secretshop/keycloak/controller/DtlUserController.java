package com.secretshop.keycloak.controller;

import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.enums.WorkGroup;
import com.secretshop.keycloak.service.DtlUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dtl/users")
@RequiredArgsConstructor
@Tag(name = "DTL Users", description = "Операции с пользователями DTL")
public class DtlUserController {

    private final DtlUserService dtlUserService;

    @Operation(summary = "Получить пользователя из DTL по ID", description = "Возвращает пользователя DTL по его UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(dtlUserService.getUser(userId));
    }

    @Operation(summary = "Получить всех пользователей из DTL (с пагинацией)", description = "Возвращает список всех пользователей DTL с поддержкой пагинации")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @Parameter(description = "Номер страницы (по умолчанию 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы (по умолчанию 20)", example = "20") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(dtlUserService.getAllUsers(page, size));
    }

    @Operation(summary = "Обновить данные пользователя в DTL", description = "Обновляет данные пользователя DTL по его UUID")
    @ApiResponse(responseCode = "200", description = "Данные пользователя успешно обновлены")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Данные для обновления пользователя", required = true) @RequestBody UserUpdateDTO updateDTO) {
        return ResponseEntity.ok(dtlUserService.updateUser(userId, updateDTO));
    }

    @Operation(summary = "Обновить баланс пользователя", description = "Обновляет баланс пользователя DTL по его UUID")
    @ApiResponse(responseCode = "200", description = "Баланс пользователя успешно обновлен")
    @PutMapping("/{userId}/balance")
    public ResponseEntity<UserDTO> updateBalance(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Информация для обновления баланса", required = true) @RequestBody BalanceUpdateDTO balanceUpdate) {
        return ResponseEntity.ok(dtlUserService.updateBalance(userId, balanceUpdate));
    }

    @Operation(summary = "Получить аватар пользователя", description = "Возвращает аватар пользователя DTL по его UUID")
    @ApiResponse(responseCode = "200", description = "Аватар пользователя получен")
    @GetMapping("/{userId}/avatar")
    public ResponseEntity<String> getUserAvatar(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(dtlUserService.getUserAvatar(userId));
    }

    @Operation(summary = "Обновить аватар пользователя", description = "Обновляет аватар пользователя DTL по его UUID")
    @ApiResponse(responseCode = "200", description = "Аватар пользователя успешно обновлен")
    @PutMapping("/{userId}/avatar")
    public ResponseEntity<UserDTO> updateAvatar(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Новый аватар", required = true) @RequestBody AvatarUpdateDTO avatarUpdate) {
        return ResponseEntity.ok(dtlUserService.updateAvatar(userId, avatarUpdate));
    }

    @Operation(summary = "Получить рабочую группу пользователя", description = "Возвращает рабочую группу пользователя DTL по его UUID")
    @ApiResponse(responseCode = "200", description = "Рабочая группа пользователя получена")
    @GetMapping("/{userId}/work-group")
    public ResponseEntity<WorkGroup> getUserWorkGroup(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(dtlUserService.getUserWorkGroup(userId));
    }
}
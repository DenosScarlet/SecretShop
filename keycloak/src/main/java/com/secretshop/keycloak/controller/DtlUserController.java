package com.secretshop.keycloak.controller;

import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.enums.WorkGroup;
import com.secretshop.keycloak.service.DtlUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dtl/users")
@RequiredArgsConstructor
public class DtlUserController {

    private final DtlUserService dtlUserService;

    // Получить пользователя из DTL по ID
    @GetMapping("/{userId}")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(dtlUserService.getUser(userId));
    }

    // Получить всех пользователей из DTL (с пагинацией)
    @GetMapping
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(dtlUserService.getAllUsers(page, size));
    }

    // Обновить данные пользователя в DTL
    @PutMapping("/{userId}")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateDTO updateDTO) {
        return ResponseEntity.ok(dtlUserService.updateUser(userId, updateDTO));
    }

    // Обновить баланс пользователя
    @PatchMapping("/{userId}/balance")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<UserDTO> updateBalance(
            @PathVariable UUID userId,
            @RequestBody BalanceUpdateDTO balanceUpdate) {
        return ResponseEntity.ok(dtlUserService.updateBalance(userId, balanceUpdate));
    }


    // Получить аватар пользователя
    @GetMapping("/{userId}/avatar")
    public ResponseEntity<String> getUserAvatar(@PathVariable UUID userId) {
        return ResponseEntity.ok(dtlUserService.getUserAvatar(userId));
    }

    // Обновить аватар пользователя
    @PutMapping("/{userId}/avatar")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<UserDTO> updateAvatar(
            @PathVariable UUID userId,
            @RequestBody AvatarUpdateDTO avatarUpdate) {
        return ResponseEntity.ok(dtlUserService.updateAvatar(userId, avatarUpdate));
    }

    // Получить рабочую группу пользователя
    @GetMapping("/{userId}/work-group")
    public ResponseEntity<WorkGroup> getUserWorkGroup(@PathVariable UUID userId) {
        return ResponseEntity.ok(dtlUserService.getUserWorkGroup(userId));
    }
}

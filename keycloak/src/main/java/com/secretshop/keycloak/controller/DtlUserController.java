package com.secretshop.keycloak.controller;

import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.enums.WorkGroup;
import com.secretshop.keycloak.service.DtlUserService;
import com.secretshop.keycloak.service.impl.UserEventClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dtl/users")
@RequiredArgsConstructor
@Tag(name = "DTL Users", description = "Операции с пользователями DTL")
public class DtlUserController {

    private final DtlUserService dtlUserService;
    private final UserEventClient userEventClient;

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

    @Operation(summary = "Загрузить аватар пользователя", description = "Загружает аватар в хранилище и обновляет запись пользователя")
    @ApiResponse(responseCode = "200", description = "Аватар успешно обновлен")
    @PostMapping(value = "/{userId}/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> uploadUserAvatar(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Файл аватара", required = true)
            @RequestPart("file") MultipartFile file) {

        // Генерируем уникальное имя файла
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = userId.toString() + "." + extension;

        // Загружаем файл в Minio через REST клиент
        userEventClient.uploadAvatar(file, fileName, "secretshop");

        // Формируем URL для скачивания
        String avatarUrl = "/api/files/download?fileName=" + fileName + "&bucketName=secretshop";

        // Обновляем аватар пользователя
        AvatarUpdateDTO avatarUpdate = new AvatarUpdateDTO();
        avatarUpdate.setAvatarUrl(avatarUrl);
        return updateAvatar(userId, avatarUpdate);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Имя файла", required = true, example = "userId_timestamp.jpg")
            @RequestParam("fileName") String fileName,
            @Parameter(description = "Название бакета", example = "secretshop")
            @RequestParam(value = "bucketName", required = false, defaultValue = "secretshop") String bucketName) {
        try {
            // Вызываем REST API сервиса 8580 для скачивания файла
            byte[] fileContent = userEventClient.downloadFile(fileName, bucketName);

            // Создаём ресурс из массива байтов
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            // Настраиваем заголовки для предотвращения кэширования
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            headers.set(HttpHeaders.ETAG, null); // Отключаем ETag
            headers.add(HttpHeaders.LAST_MODIFIED, new Date().toString()); // Обновляем Last-Modified

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {

            throw new RuntimeException("Ошибка скачивания файла", e);
        }
    }




}
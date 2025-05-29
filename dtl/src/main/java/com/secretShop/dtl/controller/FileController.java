package com.secretShop.dtl.controller;

import com.secretShop.dtl.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Tag(name = "Управление файлами", description = "API для работы с файлами в хранилище")
@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private MinioService minioService;

    @Operation(summary = "Загрузить файл", description = "Загружает файл в облачное хранилище")
    @ApiResponse(responseCode = "200", description = "Файл успешно загружен")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "Файл для загрузки", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Название бакета", example = "my-bucket")
            @RequestParam(required = false, defaultValue = "my-bucket") String bucketName) {
        try {
            minioService.uploadFile(
                    bucketName,
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Скачать файл", description = "Скачивает файл из облачного хранилища")
    @ApiResponse(responseCode = "200", description = "Файл успешно скачан")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "Имя файла", required = true, example = "avatar.jpg")
            @RequestParam String fileName,
            @Parameter(description = "Название бакета", example = "my-bucket")
            @RequestParam(required = false, defaultValue = "my-bucket") String bucketName) {
        try {
            InputStream fileStream = minioService.downloadFile(bucketName, fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            throw new RuntimeException("Download failed: " + e.getMessage(), e);
        }
    }

    @Operation(summary = "Удалить файл", description = "Удаляет файл из облачного хранилища")
    @ApiResponse(responseCode = "200", description = "Файл успешно удален")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "Имя файла", required = true, example = "avatar.jpg")
            @RequestParam String fileName,
            @Parameter(description = "Название бакета", example = "my-bucket")
            @RequestParam(required = false, defaultValue = "my-bucket") String bucketName) {
        try {
            minioService.deleteFile(bucketName, fileName);
            return ResponseEntity.ok("File deleted successfully: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Delete failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Проверить существование файла", description = "Проверяет наличие файла в хранилище")
    @ApiResponse(responseCode = "200", description = "Проверка выполнена")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> fileExists(
            @Parameter(description = "Имя файла", required = true, example = "avatar.jpg")
            @RequestParam String fileName,
            @Parameter(description = "Название бакета", example = "my-bucket")
            @RequestParam(required = false, defaultValue = "my-bucket") String bucketName) {
        try {
            InputStream testStream = minioService.downloadFile(bucketName, fileName);
            if (testStream != null) {
                testStream.close();
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}

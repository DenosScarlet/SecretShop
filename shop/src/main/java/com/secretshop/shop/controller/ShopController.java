package com.secretshop.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.DTO.OperationDTO;
import com.secretshop.shop.DTO.UpdateOperationDTO;
import com.secretshop.shop.enums.Status;
import com.secretshop.shop.enums.Type;
import com.secretshop.shop.service.ShopService;
import com.secretshop.shop.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shop")
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
        allowedHeaders = "*"
)
@Tag(name = "Shop", description = "Операции с товарами, файлами и покупками")
public class ShopController {

    private final ShopService shopService;
    private final JwtUtils jwtUtils;

    public ShopController(ShopService shopService, JwtUtils jwtUtils) {
        this.shopService = shopService;
        this.jwtUtils = jwtUtils;
    }

    @Operation(summary = "Получить товар по ID", description = "Возвращает подробную информацию о товаре по его идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар найден"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @GetMapping("/item/{id}")
    public ItemDTO getItem(
            @Parameter(description = "UUID товара", required = true) @PathVariable UUID id) {
        return shopService.getItemFromDtl(id);
    }

    @Operation(summary = "Получить список всех товаров", description = "Возвращает список всех товаров")
    @ApiResponse(responseCode = "200", description = "Список товаров успешно получен")
    @GetMapping("/items")
    public List<ItemDTO> getAllItems() {
        return shopService.getAllItemsFromDtl();
    }

    @Operation(summary = "Добавить товар с файлом", description = "Добавляет новый товар с возможностью загрузки файла")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    })
    @PostMapping(value = "/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDTO> addItemWithFile(
            @Parameter(description = "Данные товара", required = true)
            @RequestParam("item") String itemJson,

            @Parameter(description = "Файл (опционально)")
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        // Десериализация JSON в DTO
        ObjectMapper objectMapper = new ObjectMapper();
        ItemDTO itemDTO;

        try {
            itemDTO = objectMapper.readValue(itemJson, ItemDTO.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Неверный формат JSON для товара: " + e.getMessage()
            );
        }

        ItemDTO createdItem = shopService.addItemWithFile(itemDTO, file);
        return ResponseEntity.ok(createdItem);
    }

    @Operation(summary = "Обновить товар", description = "Обновляет информацию о товаре по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Товар успешно обновлен")
    @PutMapping("/item/{id}")
    public ItemDTO updateItem(
            @Parameter(description = "UUID товара", required = true) @PathVariable UUID id,
            @Parameter(description = "Обновленные данные товара", required = true) @RequestBody ItemDTO itemDTO) {
        itemDTO.setItemId(id);
        return shopService.updateItem(itemDTO);
    }

    @Operation(summary = "Удалить товар", description = "Удаляет товар по идентификатору")
    @ApiResponse(responseCode = "204", description = "Товар успешно удален")
    @DeleteMapping("/item/{id}")
    public void deleteItem(
            @Parameter(description = "UUID товара", required = true) @PathVariable UUID id) {
        shopService.deleteItem(id);
    }

    @Operation(summary = "Поиск товаров", description = "Позволяет искать товары по имени, владельцу и типу")
    @ApiResponse(responseCode = "200", description = "Список найденных товаров")
    @GetMapping("/item/search")
    public List<ItemDTO> searchItems(
            @Parameter(description = "Имя товара") @RequestParam(required = false) String name,
            @Parameter(description = "Владелец товара") @RequestParam(required = false) String owner,
            @Parameter(description = "Тип товара") @RequestParam(required = false) Type type) {
        return shopService.searchItems(name, owner, type);
    }

    @Operation(summary = "Покупка товара", description = "Позволяет пользователю купить товар по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Покупка успешна"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @PostMapping("/purchase/{itemId}")
    public ResponseEntity<String> purchaseItem(
            @Parameter(description = "UUID товара", required = true) @PathVariable UUID itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            UUID userId = UUID.fromString(jwt.getClaim("sub"));
            shopService.purchaseItem(itemId, userId);
            return ResponseEntity.ok("Purchase successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
    }

    @Operation(summary = "Получить операции пользователя", description = "Возвращает список операций пользователя по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Список операций пользователя")
    @GetMapping("/operations/user/{userId}")
    public ResponseEntity<List<OperationDTO>> getOperationsByCurrentUser(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(shopService.getOperationsByUser(userId));
    }

    @Operation(summary = "Получить операции по товару", description = "Возвращает список операций для конкретного товара")
    @ApiResponse(responseCode = "200", description = "Список операций по товару")
    @GetMapping("/operations/item/{itemId}")
    public ResponseEntity<List<OperationDTO>> getOperationsByItem(
            @Parameter(description = "UUID товара", required = true) @PathVariable UUID itemId) {
        return ResponseEntity.ok(shopService.getOperationsByItem(itemId));
    }

    @Operation(summary = "Получить операцию по ID", description = "Возвращает информацию об операции по её идентификатору")
    @ApiResponse(responseCode = "200", description = "Операция найдена")
    @GetMapping("/operations/{operationId}")
    public ResponseEntity<OperationDTO> getOperationById(
            @Parameter(description = "UUID операции", required = true) @PathVariable UUID operationId) {
        return ResponseEntity.ok(shopService.getOperationById(operationId));
    }

    @Operation(summary = "Получить все операции", description = "Возвращает список всех операций с поддержкой фильтрации по статусу и пагинации")
    @ApiResponse(responseCode = "200", description = "Список операций успешно получен")
    @GetMapping("/operations")
    public ResponseEntity<List<OperationDTO>> getAllOperations(
            @Parameter(description = "Номер страницы", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Статус операции") @RequestParam(required = false) Status status) {
        List<OperationDTO> operations = shopService.getAllOperations();
        if (status != null) {
            operations = operations.stream()
                    .filter(op -> op.getStatus() == status)
                    .toList();
        }
        List<OperationDTO> paginatedOperations = operations.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(operations.size()))
                .body(paginatedOperations);
    }

    @Operation(summary = "Обновить операцию", description = "Обновляет информацию об операции по её идентификатору")
    @ApiResponse(responseCode = "200", description = "Операция успешно обновлена")
    @PutMapping("/operation/{operationId}")
    public ResponseEntity<OperationDTO> updateOperation(
            @Parameter(description = "UUID операции", required = true) @PathVariable UUID operationId,
            @Parameter(description = "Данные для обновления операции", required = true) @RequestBody UpdateOperationDTO updateDto) {
        return ResponseEntity.ok(shopService.updateOperation(operationId, updateDto));
    }

    @Operation(summary = "Загрузить файл для товара", description = "Позволяет загрузить файл для указанного товара")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
            @ApiResponse(responseCode = "500", description = "Ошибка загрузки файла")
    })
    @PostMapping("/item/{itemId}/upload")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "UUID товара", required = true) @PathVariable UUID itemId,
            @Parameter(description = "Загружаемый файл", required = true) @RequestParam("file") MultipartFile file) {
        try {
            String result = shopService.uploadFile(itemId, file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Скачать файл товара", description = "Позволяет скачать файл, связанный с товаром")
    @ApiResponse(responseCode = "200", description = "Файл успешно скачан")
    @GetMapping("/item/{itemId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "UUID товара", required = true) @PathVariable UUID itemId,
            @Parameter(description = "Имя файла", required = true) @RequestParam String fileName) {
        return shopService.downloadFile(itemId, fileName);
    }

    @Operation(summary = "Удалить файл товара", description = "Удаляет файл, связанный с товаром")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл успешно удалён"),
            @ApiResponse(responseCode = "500", description = "Ошибка удаления файла")
    })
    @DeleteMapping("/item/{itemId}/file")
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "UUID товара", required = true)
            @PathVariable UUID itemId) {

        try {
            // Получаем имя файла из сервиса (например, по шаблону itemId + расширение)
            String fileName = itemId.toString()+".jpg";

            // Удаляем файл
            String result = shopService.deleteFile(itemId, fileName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File deletion failed: " + e.getMessage());
        }
    }
}

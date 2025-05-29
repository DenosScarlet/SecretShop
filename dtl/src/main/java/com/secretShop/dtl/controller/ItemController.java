package com.secretShop.dtl.controller;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.enums.Type;
import com.secretShop.dtl.repository.ItemRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Управление товарами", description = "API для работы с товарами")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;

    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров")
    @ApiResponse(responseCode = "200", description = "Товары получены")
    @GetMapping("/item")
    public List<Item> getAllItem() {
        return itemRepository.findAll();
    }

    @Operation(summary = "Получить товар по ID", description = "Возвращает данные конкретного товара")
    @ApiResponse(responseCode = "200", description = "Товар найден")
    @GetMapping("/item/{id}")
    public Item getItemById(
            @Parameter(description = "ID товара", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id){
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден."));
    }

    @Operation(summary = "Создать товар", description = "Добавляет новый товар в систему")
    @ApiResponse(responseCode = "200", description = "Товар создан")
    @PostMapping("/item")
    public Item createItem(
            @RequestBody Item item){
        return itemRepository.save(item);
    }

    @Operation(summary = "Удалить товар", description = "Удаляет товар из системы")
    @ApiResponse(responseCode = "200", description = "Товар удален")
    @DeleteMapping("/item/{id}")
    public void deleteItem(
            @Parameter(description = "ID товара", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id) {
        itemRepository.deleteById(id);
    }

    @Operation(summary = "Обновить товар", description = "Изменяет данные существующего товара")
    @ApiResponse(responseCode = "200", description = "Товар обновлен")
    @PutMapping("/item/{id}")
    public Item updateItem(
            @Parameter(description = "ID товара", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id,
            @RequestBody Item item) {
        item.setItemId(id);
        return itemRepository.save(item);
    }

    @Operation(summary = "Поиск товаров", description = "Фильтрует товары по различным параметрам")
    @ApiResponse(responseCode = "200", description = "Товары найдены")
    @GetMapping("/item/search")
    public List<Item> searchItems(
            @Parameter(description = "Название товара", example = "Планшет")
            @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "Владелец товара", example = "SecretShop")
            @RequestParam(name = "owner", required = false) String owner,
            @Parameter(description = "Тип товара", example = "DEVICE")
            @RequestParam(name = "type", required = false) Type type) {
        return itemRepository.searchItems(name, owner, type);
    }
}

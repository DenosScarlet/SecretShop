package com.secretshop.shop.controller;

import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.enums.Type;
import com.secretshop.shop.service.ShopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop")
@CrossOrigin(origins = "http://localhost:3000")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService){
        this.shopService = shopService;
    }

    @GetMapping("/item/{id}")
    public ItemDTO getItem(@PathVariable UUID id){
        return shopService.getItemFromDtl(id);
    }

    @GetMapping("/items")
    public List<ItemDTO> getAllItems() {
        return shopService.getAllItemsFromDtl();
    }

    @PostMapping("/item")
    public ItemDTO addItem(@RequestBody ItemDTO itemDTO) {
        return shopService.addItem(itemDTO);
    }

    @PutMapping("/item/{id}")
    public ItemDTO updateItem(@PathVariable UUID id, @RequestBody ItemDTO itemDTO) {
        itemDTO.setItem_id(id); // Устанавливаем ID из пути запроса
        return shopService.updateItem(itemDTO);
    }

    @DeleteMapping("/item/{id}")
    public void deleteItem(@PathVariable UUID id) {
        shopService.deleteItem(id);
    }

    @GetMapping("/item/search")
    public List<ItemDTO> searchItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) Type type) {
        return shopService.searchItems(name, owner, type);
    }

}

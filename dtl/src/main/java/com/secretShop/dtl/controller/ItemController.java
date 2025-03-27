package com.secretShop.dtl.controller;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.repository.ItemRepository;
import com.secretShop.dtl.service.dto.QuestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    @GetMapping("/item")
    public List<Item> getAllItem() {
        return itemRepository.findAll();
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable UUID id){
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

    }

    @PostMapping("/item")
    public Item createItem(@RequestBody Item item){
        return itemRepository.save(item);
    }



}

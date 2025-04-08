package com.secretShop.dtl.controller;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.enums.Type;
import com.secretShop.dtl.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    @GetMapping("/item")
    public List<Item> getAllItem() {
        return itemRepository.findAll();
    }

    @GetMapping("/item/{id}")
    public Item getItemById(@PathVariable("id") UUID id){
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

    }

    @PostMapping("/item")
    public Item createItem(@RequestBody Item item){
        return itemRepository.save(item);
    }

    @DeleteMapping("/item/{id}")
    public void deleteItem(@PathVariable("id") UUID id) {
        itemRepository.deleteById(id);
    }

    @PutMapping("/item/{id}")
    public Item updateItem(@PathVariable("id") UUID id, @RequestBody Item item) {
        item.setItem_id(id);
        return itemRepository.save(item);
    }

    @GetMapping("/item/search")
    public List<Item> searchItems(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "owner", required = false) String owner,
            @RequestParam(name = "type", required = false) Type type) {
        return itemRepository.searchItems(name, owner, type);
    }

}

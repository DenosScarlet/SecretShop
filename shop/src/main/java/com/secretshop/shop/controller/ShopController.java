package com.secretshop.shop.controller;

import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.service.ShopService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService){
        this.shopService = shopService;
    }

    @GetMapping("/item/{id}")
    public ItemDTO getItem(@PathVariable UUID id){
        return shopService.getItemFromDtl(id);
    }

    @PostMapping("/item")
    public ItemDTO addItem(@RequestBody ItemDTO itemDTO) {
        return shopService.addItem(itemDTO);
    }

}

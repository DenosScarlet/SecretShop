package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.OperationOnItemDTO;
import com.secretShop.dtl.DTO.PurchaseItemDTO;
import com.secretShop.dtl.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperationOnItemDTO purchaseItem(@RequestBody PurchaseItemDTO purchaseItemDTO) {
        return purchaseService.purchaseItem(purchaseItemDTO);
    }
}

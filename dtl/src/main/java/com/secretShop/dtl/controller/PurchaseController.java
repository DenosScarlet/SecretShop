package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.OperationOnItemDTO;
import com.secretShop.dtl.DTO.PurchaseItemDTO;
import com.secretShop.dtl.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Покупки", description = "API для совершения покупок")
@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @Operation(summary = "Купить товар", description = "Выполняет покупку товара сотрудником")
    @ApiResponse(responseCode = "201", description = "Покупка совершена")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperationOnItemDTO purchaseItem(
            @RequestBody PurchaseItemDTO purchaseItemDTO) {
        return purchaseService.purchaseItem(purchaseItemDTO);
    }
}

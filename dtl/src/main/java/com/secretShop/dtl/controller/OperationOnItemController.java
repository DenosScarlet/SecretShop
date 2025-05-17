package com.secretShop.dtl.controller;

import com.secretShop.dtl.service.dto.CreateOperationOnItemDTO;
import com.secretShop.dtl.service.dto.OperationOnItemDTO;
import com.secretShop.dtl.service.OperationOnItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
public class OperationOnItemController {

    private final OperationOnItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OperationOnItemDTO createOperation(@RequestBody CreateOperationOnItemDTO dto) {
        return service.createOperation(dto);
    }

    @GetMapping("/{id}")
    public OperationOnItemDTO getOperation(@PathVariable UUID id) {
        return service.getOperationById(id);
    }

    @GetMapping
    public List<OperationOnItemDTO> getAllOperations() {
        return service.getAllOperations();
    }

    @GetMapping("/user/{userId}")
    public List<OperationOnItemDTO> getOperationsByUser(@PathVariable UUID userId) {
        return service.getOperationsByUserId(userId);
    }

    @GetMapping("/item/{itemId}")
    public List<OperationOnItemDTO> getOperationsByItem(@PathVariable UUID itemId) {
        return service.getOperationsByItemId(itemId);
    }

    @PutMapping("/{id}")
    public OperationOnItemDTO updateOperation(@PathVariable UUID id, @RequestBody OperationOnItemDTO dto) {
        return service.updateOperation(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOperation(@PathVariable UUID id) {
        service.deleteOperation(id);
    }
}
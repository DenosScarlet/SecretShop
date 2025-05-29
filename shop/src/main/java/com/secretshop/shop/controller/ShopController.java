package com.secretshop.shop.controller;

import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.DTO.OperationDTO;
import com.secretshop.shop.DTO.UpdateOperationDTO;
import com.secretshop.shop.enums.Status;
import com.secretshop.shop.enums.Type;
import com.secretshop.shop.service.ShopService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.secretshop.shop.util.JwtUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private final ShopService shopService;
    private final JwtUtils jwtUtils;

    public ShopController(ShopService shopService, JwtUtils jwtUtils) {
        this.shopService = shopService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/item/{id}")
    public ItemDTO getItem(@PathVariable UUID id){
        return shopService.getItemFromDtl(id);
    }

    @GetMapping("/items")
    public List<ItemDTO> getAllItems() {
        return shopService.getAllItemsFromDtl();
    }

    @PostMapping(value = "/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDTO> addItemWithFile(
            @RequestPart("item") ItemDTO itemDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        ItemDTO createdItem = shopService.addItemWithFile(itemDTO, file);
        return ResponseEntity.ok(createdItem);
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

    @PostMapping("/purchase/{itemId}")
    public ResponseEntity<String> purchaseItem(@PathVariable UUID itemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            UUID userId = UUID.fromString(jwt.getClaim("sub")); // или другой claim
            shopService.purchaseItem(itemId, userId);
            return ResponseEntity.ok("Purchase successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
    }

    @GetMapping("/operations/user/{userId}")
    public ResponseEntity<List<OperationDTO>> getOperationsByCurrentUser(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(shopService.getOperationsByUser(userId));
    }

    @GetMapping("/operations/item/{itemId}")
    public ResponseEntity<List<OperationDTO>> getOperationsByItem(
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(shopService.getOperationsByItem(itemId));
    }

    @GetMapping("/operations/{operationId}")
    public ResponseEntity<OperationDTO> getOperationById(
            @PathVariable UUID operationId) {
        return ResponseEntity.ok(shopService.getOperationById(operationId));
    }

    @GetMapping("/operations")
    public ResponseEntity<List<OperationDTO>> getAllOperations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Status status) {

        List<OperationDTO> operations = shopService.getAllOperations();

        // Применяем фильтрацию по статусу (если указана)
        if (status != null) {
            operations = operations.stream()
                    .filter(op -> op.getStatus() == status)
                    .toList();
        }

        // Применяем пагинацию
        List<OperationDTO> paginatedOperations = operations.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(operations.size()))
                .body(paginatedOperations);
    }


    @PutMapping("/operation/{operationId}")
    public ResponseEntity<OperationDTO> updateOperation(@PathVariable UUID operationId,
            @RequestBody UpdateOperationDTO updateDto) {
        return ResponseEntity.ok(shopService.updateOperation(operationId, updateDto));
    }

    @PostMapping("/item/{itemId}/upload")
    public ResponseEntity<String> uploadFile(
            @PathVariable UUID itemId,
            @RequestParam("file") MultipartFile file) {
        try {
            String result = shopService.uploadFile(itemId, file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/item/{itemId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable UUID itemId,
            @RequestParam String fileName) {
        return shopService.downloadFile(itemId, fileName);
    }

    @DeleteMapping("/item/{itemId}/file")
    public ResponseEntity<String> deleteFile(
            @PathVariable UUID itemId,
            @RequestParam String fileName) {
        try {
            String result = shopService.deleteFile(itemId, fileName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File deletion failed: " + e.getMessage());
        }
    }

}

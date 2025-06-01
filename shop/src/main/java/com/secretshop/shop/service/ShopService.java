package com.secretshop.shop.service;

import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.DTO.OperationDTO;
import com.secretshop.shop.DTO.PurchaseDTO;
import com.secretshop.shop.DTO.UpdateOperationDTO;
import com.secretshop.shop.enums.Type;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ShopService {
    private final DtlServiceClient dtlServiceClient;

    public ShopService(DtlServiceClient dtlServiceClient){
        this.dtlServiceClient = dtlServiceClient;
    }

    public ItemDTO getItemFromDtl(UUID id){
        return dtlServiceClient.getItemById(id);
    }

    public List<ItemDTO> getAllItemsFromDtl() {
        return dtlServiceClient.getAllItem();
    }

    @Transactional
    public ItemDTO addItemWithFile(ItemDTO itemDTO, MultipartFile file) {
        // 1. Создаем предмет в DTL и получаем его ID
        ItemDTO createdItem = dtlServiceClient.createItem(itemDTO);
        UUID itemId = createdItem.getItem_id();

        // 2. Если файл передан, загружаем его и переименовываем под itemId
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = itemId + fileExtension; // Например: "123e4567-e89b-12d3-a456-426614174000.jpg"

            // 3. Загружаем файл в MinIO через DTL
            dtlServiceClient.uploadFileForItem(itemId, file);
        }

        return createdItem;
    }

    public ItemDTO updateItem(ItemDTO itemDTO) {
        return dtlServiceClient.updateItem(itemDTO);
    }

    public void deleteItem(UUID id) {
        dtlServiceClient.deleteItem(id);
    }

    public List<ItemDTO> searchItems(String name, String owner, Type type) {
        String searchName = (name != null && !name.isEmpty()) ? name : null;
        String searchOwner = (owner != null && !owner.isEmpty()) ? owner : null;

        return dtlServiceClient.searchItems(searchName, searchOwner, type);
    }

    public void purchaseItem(UUID itemId, UUID userId) {
        PurchaseDTO purchaseDTO = new PurchaseDTO(itemId, userId);
        dtlServiceClient.purchaseItem(purchaseDTO);
    }

    @Cacheable(value = "operations", key = "#userId")
    public List<OperationDTO> getOperationsByUser(UUID userId) {
        return dtlServiceClient.getOperationsByUser(userId);
    }

    public List<OperationDTO> getOperationsByItem(UUID itemId) {
        return dtlServiceClient.getOperationsByItem(itemId);
    }

    public OperationDTO getOperationById(UUID operationId) {
        return dtlServiceClient.getOperationById(operationId);
    }

    public List<OperationDTO> getAllOperations() {
        return dtlServiceClient.getAllOperations();
    }

    @Transactional
    public OperationDTO updateOperation(UUID operationId,UpdateOperationDTO updateDto) {
        // Можно добавить дополнительную бизнес-логику перед обновлением
        return dtlServiceClient.updateOperation(operationId, updateDto);
    }

    @Transactional
    public String uploadFile(UUID itemId, MultipartFile file) {
        return dtlServiceClient.uploadFile(itemId, file);
    }

    public ResponseEntity<InputStreamResource> downloadFile(UUID itemId, String fileName) {
        return dtlServiceClient.downloadFile(itemId, fileName);
    }

    @Transactional
    public String deleteFile(UUID itemId, String fileName) {
        return dtlServiceClient.deleteFile(itemId, fileName);
    }

}

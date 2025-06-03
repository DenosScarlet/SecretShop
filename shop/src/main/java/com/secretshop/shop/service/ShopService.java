package com.secretshop.shop.service;

import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.DTO.OperationDTO;
import com.secretshop.shop.DTO.PurchaseDTO;
import com.secretshop.shop.DTO.UpdateOperationDTO;
import com.secretshop.shop.enums.Type;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
    public ItemDTO addItemWithFile(ItemDTO itemDTO, MultipartFile file) throws IOException {
        // Валидация входных данных
        validateItemDTO(itemDTO);

        // Создаем предмет в DTL и получаем его ID
        ItemDTO createdItem = dtlServiceClient.createItem(itemDTO);
        UUID itemId = createdItem.getItemId();

        // Если файл передан, загружаем его
        if (file != null && !file.isEmpty()) {
            // Генерируем новое имя файла на основе ID
            String newFileName = generateFileName(itemId, file.getOriginalFilename());

            // Создаем переименованную реализацию MultipartFile через лямбду
            MultipartFile renamedFile = new MultipartFile() {
                @Override
                public String getName() { return file.getName(); }

                @Override
                public String getOriginalFilename() { return newFileName; }

                @Override
                public String getContentType() { return file.getContentType(); }

                @Override
                public boolean isEmpty() { return file.isEmpty(); }

                @Override
                public long getSize() { return file.getSize(); }

                @Override
                public byte[] getBytes() throws IOException { return file.getBytes(); }

                @Override
                public InputStream getInputStream() throws IOException { return file.getInputStream(); }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    file.transferTo(dest);
                }
            };

            // Загружаем файл в MinIO через DTL
            dtlServiceClient.uploadFile(itemId, renamedFile);
        }

        return createdItem;
    }
    // Генерация имени файла на основе ID предмета


    private void validateItemDTO(ItemDTO itemDTO) {
        if (itemDTO.getItemName() == null || itemDTO.getItemName().isBlank()) {
            throw new IllegalArgumentException("Название товара обязательно");
        }
        if (itemDTO.getCost() == null || itemDTO.getCost() <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        // Добавьте другие необходимые проверки
    }

    private String generateFileName(UUID itemId, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return itemId.toString() + extension;
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

    public ResponseEntity<byte[]> downloadFile(UUID itemId, String fileName) {
        return dtlServiceClient.downloadFile(itemId, fileName);
    }

    @Transactional
    public String deleteFile(UUID itemId, String fileName) {
        return dtlServiceClient.deleteFile(itemId, fileName);
    }



}

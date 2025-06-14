package com.secretshop.shop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretshop.shop.DTO.*;
import com.secretshop.shop.enums.Type;
import com.secretshop.shop.kafka.RequestReplyProducer;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class DtlServiceClient {
    private final RequestReplyProducer producer;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DtlServiceClient(RequestReplyProducer producer, ObjectMapper objectMapper) {
        this.producer = producer;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8580")
                .messageConverters(converters -> {
                    converters.add(new MappingJackson2HttpMessageConverter());
                })
                .build();
    }

    public ItemDTO getItemById(UUID id) {
        try {
            ShopRequest request = new ShopRequest("GET_ITEM_BY_ID", id.toString(), null);
            return producer.sendAndReceive(request, ItemDTO.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public List<ItemDTO> getAllItem() {
        try {
            ShopRequest request = new ShopRequest("GET_ALL_ITEMS", null, null);
            return producer.sendAndReceive(request, List.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public ItemDTO createItem(ItemDTO itemDTO) {
        try {
            ShopRequest request = new ShopRequest("CREATE_ITEM", itemDTO, null);
            return producer.sendAndReceive(request, ItemDTO.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public ItemDTO updateItem(ItemDTO itemDTO) {
        try {
            ShopRequest request = new ShopRequest("UPDATE_ITEM", itemDTO, null);
            return producer.sendAndReceive(request, ItemDTO.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public void deleteItem(UUID id) {
        try {
            ShopRequest request = new ShopRequest("DELETE_ITEM", id.toString(), null);
            producer.sendAndReceive(request, String.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public List<ItemDTO> searchItems(String name, String owner, Type type) {
        try {
            SearchRequest searchRequest = new SearchRequest(name, owner, type);
            ShopRequest request = new ShopRequest("SEARCH_ITEMS", searchRequest, null);
            return producer.sendAndReceive(request, List.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public void purchaseItem(PurchaseDTO purchaseDTO) {
        try {
            ShopRequest request = new ShopRequest("PURCHASE_ITEM", purchaseDTO, null);
            producer.sendAndReceive(request, Void.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public List<OperationDTO> getOperationsByUser(UUID userId) {
        try {
            ShopRequest request = new ShopRequest("GET_OPERATIONS_BY_USER", userId.toString(), null);
            return producer.sendAndReceive(request, List.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public List<OperationDTO> getOperationsByItem(UUID itemId) {
        try {
            ShopRequest request = new ShopRequest("GET_OPERATIONS_BY_ITEM", itemId.toString(), null);
            return producer.sendAndReceive(request, List.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public OperationDTO getOperationById(UUID operationId) {
        try {
            ShopRequest request = new ShopRequest("GET_OPERATION_BY_ID", operationId.toString(), null);
            return producer.sendAndReceive(request, OperationDTO.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public List<OperationDTO> getAllOperations() {
        try {
            ShopRequest request = new ShopRequest("GET_ALL_OPERATIONS", null, null);
            return producer.sendAndReceive(request, List.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    public OperationDTO updateOperation(UUID operationId, UpdateOperationDTO updateDto) {
        try {
            UpdateOperationRequest requestPayload = new UpdateOperationRequest(operationId, updateDto);
            ShopRequest request = new ShopRequest("UPDATE_OPERATION", requestPayload, null);
            return producer.sendAndReceive(request, OperationDTO.class).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka request failed", e);
        }
    }

    // Методы для работы с файлами оставляем как REST
    public String uploadFile(UUID itemId, MultipartFile file) {
        try {
            Resource fileResource = new InputStreamResource(file.getInputStream()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
                @Override
                public long contentLength() {
                    return file.getSize();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            return restClient.post()
                    .uri("/api/files/upload?itemId={itemId}", itemId)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    public ResponseEntity<byte[]> downloadFile(UUID itemId, String fileName) {
        return restClient.get()
                .uri("/api/files/download?itemId={itemId}&fileName={fileName}", itemId, fileName)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .toEntity(byte[].class);
    }

    public String deleteFile(UUID itemId, String fileName) {
        return restClient.delete()
                .uri("/api/files/delete?itemId={itemId}&fileName={fileName}", itemId, fileName)
                .retrieve()
                .body(String.class);
    }

    // Внутренние DTO для передачи нескольких параметров
    private static class SearchRequest {
        public String name;
        public String owner;
        public Type type;

        public SearchRequest(String name, String owner, Type type) {
            this.name = name;
            this.owner = owner;
            this.type = type;
        }
    }

    private static class UpdateOperationRequest {
        public UUID operationId;
        public UpdateOperationDTO updateDto;

        public UpdateOperationRequest(UUID operationId, UpdateOperationDTO updateDto) {
            this.operationId = operationId;
            this.updateDto = updateDto;
        }
    }
}
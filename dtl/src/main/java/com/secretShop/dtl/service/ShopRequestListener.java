package com.secretShop.dtl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secretShop.dtl.DTO.*;
import com.secretShop.dtl.enums.Type;
import com.secretShop.dtl.service.interfaces.ItemService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopRequestListener {
    private final KafkaTemplate<String, ShopRequest> singleKafkaTemplate;
    private final KafkaTemplate<String, List<ShopRequest>> listKafkaTemplate;
    private final ItemService itemService;
    private final OperationOnItemService operationOnItemService;
    private final PurchaseService purchaseService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "shop-requests", containerFactory = "shopKafkaListenerContainerFactory")
    public void listen(ShopRequest request) {
        String operation = request.getOperation();
        Object payload = request.getPayload();
        String correlationId = request.getCorrelationId();
        Object result = null;
        boolean isListResponse = false;

        try {
            switch (operation) {
                case "GET_ITEM_BY_ID":
                    result = itemService.findById(convertToUUID(payload));
                    break;

                case "GET_ALL_ITEMS":
                    result = itemService.findAll();
                    isListResponse = true;
                    break;

                case "CREATE_ITEM":
                    result = itemService.save(objectMapper.convertValue(payload, ItemDTO.class));
                    break;

                case "UPDATE_ITEM":
                    result = itemService.save(objectMapper.convertValue(payload, ItemDTO.class));
                    break;

                case "DELETE_ITEM":
                    itemService.deleteById(convertToUUID(payload));
                    result = new SuccessResponse("Item deleted successfully");
                    break;

                case "SEARCH_ITEMS":
                    ShopSearchRequest searchRequest = objectMapper.convertValue(payload, ShopSearchRequest.class);
                    result = itemService.searchItems(
                            searchRequest.getName(),
                            searchRequest.getOwner(),
                            searchRequest.getType()
                    );
                    isListResponse = true;
                    break;

                case "PURCHASE_ITEM":
                    PurchaseItemDTO purchaseItemDTO = objectMapper.convertValue(payload, PurchaseItemDTO.class);
                    result = purchaseService.purchaseItem(purchaseItemDTO);
                    break;

                case "GET_OPERATIONS_BY_USER":
                    result = operationOnItemService.getOperationsByUserId(convertToUUID(payload));
                    isListResponse = true;
                    break;

                case "GET_OPERATIONS_BY_ITEM":
                    result = operationOnItemService.getOperationsByItemId(convertToUUID(payload));
                    isListResponse = true;
                    break;

                case "GET_OPERATION_BY_ID":
                    result = operationOnItemService.getOperationById(convertToUUID(payload));
                    break;

                case "GET_ALL_OPERATIONS":
                    result = operationOnItemService.getAllOperations();
                    isListResponse = true;
                    break;

                case "UPDATE_OPERATION":
                    UpdateOperationRequest updateRequest = objectMapper.convertValue(payload, UpdateOperationRequest.class);
                    result = operationOnItemService.updateOperation(
                            updateRequest.getOperationId(),
                            updateRequest.getUpdateDto()
                    );
                    break;

                default:
                    result = new ErrorResponse("Unsupported operation: " + operation);
            }

            if (isListResponse) {
                List<ShopRequest> responseList = List.of(new ShopRequest(operation, result, correlationId));
                listKafkaTemplate.send("shop-responses-list", correlationId, responseList);
            } else {
                singleKafkaTemplate.send("shop-responses-single", correlationId, new ShopRequest(operation, result, correlationId));
            }
        } catch (Exception e) {
            singleKafkaTemplate.send("shop-responses-single", correlationId,
                    new ShopRequest(operation, new ErrorResponse("Error: " + e.getMessage()), correlationId));
        }
    }

    private UUID convertToUUID(Object payload) {
        if (payload instanceof UUID) {
            return (UUID) payload;
        } else if (payload instanceof String) {
            try {
                return UUID.fromString((String) payload);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format: " + payload, e);
            }
        } else {
            throw new IllegalArgumentException("Invalid payload type for UUID: " + payload.getClass());
        }
    }

    @Data
    public static class ShopSearchRequest {
        private String name;
        private String owner;
        private Type type;
    }

    @Data
    public static class UpdateOperationRequest {
        private UUID operationId;
        private UpdateOperationDTO updateDto;
    }

    @Data
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    @Data
    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}
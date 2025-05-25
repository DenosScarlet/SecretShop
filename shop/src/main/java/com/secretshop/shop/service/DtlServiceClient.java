package com.secretshop.shop.service;

import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.DTO.OperationDTO;
import com.secretshop.shop.DTO.PurchaseDTO;
import com.secretshop.shop.DTO.UpdateOperationDTO;
import com.secretshop.shop.enums.Type;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DtlServiceClient {
    private final RestClient restClient;

    public DtlServiceClient(){
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8580")
                .messageConverters(converters -> {
                    converters.add(new MappingJackson2HttpMessageConverter());
                    // другие необходимые конвертеры
                })
                .build();
    }

    public ItemDTO getItemById(UUID id){
        return restClient.get()
                .uri("/api/item/{id}", id)
                .retrieve()
                .body(ItemDTO.class);
    }

    public List<ItemDTO> getAllItem(){
        return restClient.get()
                .uri("/api/item")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public ItemDTO createItem(ItemDTO itemDTO) {
        return restClient.post()
                .uri("/api/item")
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemDTO)
                .retrieve()
                .body(ItemDTO.class);
    }

    public ItemDTO updateItem(ItemDTO itemDTO) {
        return restClient.put()
                .uri("/api/item/{id}", itemDTO.getItem_id())
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemDTO)
                .retrieve()
                .body(ItemDTO.class);
    }

    public void deleteItem(UUID id) {
        restClient.delete()
                .uri("/api/item/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ItemDTO> searchItems(String name, String owner, Type type) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/item/search")
                        .queryParamIfPresent("name", Optional.ofNullable(name))
                        .queryParamIfPresent("owner", Optional.ofNullable(owner))
                        .queryParamIfPresent("type", Optional.ofNullable(type))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public void purchaseItem(PurchaseDTO purchaseDTO) {
        restClient.post()
                .uri("/api/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .body(purchaseDTO)
                .retrieve()
                .toBodilessEntity();
    }


    public List<OperationDTO> getOperationsByUser(UUID userId) {
        return restClient.get()
                .uri("/api/operations/user/{userId}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<OperationDTO> getOperationsByItem(UUID itemId) {
        return restClient.get()
                .uri("/api/operations/item/{itemId}", itemId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public OperationDTO getOperationById(UUID operationId) {
        return restClient.get()
                .uri("/api/operations/{id}", operationId)
                .retrieve()
                .body(OperationDTO.class);
    }

    public List<OperationDTO> getAllOperations() {
        return restClient.get()
                .uri("/api/operations")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public OperationDTO updateOperation(UUID operationId, UpdateOperationDTO updateDto) {
        return restClient.put()
                .uri("/api/operations/{id}", operationId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateDto)
                .retrieve()
                .body(OperationDTO.class);
    }

}

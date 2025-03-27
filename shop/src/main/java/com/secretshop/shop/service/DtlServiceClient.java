package com.secretshop.shop.service;

import com.secretshop.shop.DTO.ItemDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.util.List;
import java.util.UUID;

@Service
public class DtlServiceClient {
    private final RestClient restClient;

    public DtlServiceClient(){
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8181")
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
}

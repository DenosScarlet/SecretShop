package com.secretshop.shop.service;

import com.secretshop.shop.DTO.ItemDTO;
import org.springframework.stereotype.Service;

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
}

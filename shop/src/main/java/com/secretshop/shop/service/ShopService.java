package com.secretshop.shop.service;

import com.secretshop.shop.DTO.ItemDTO;
import com.secretshop.shop.enums.Type;
import org.springframework.stereotype.Service;

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

    public ItemDTO addItem(ItemDTO itemDTO) {

        return dtlServiceClient.createItem(itemDTO);
    }

    public ItemDTO updateItem(ItemDTO itemDTO) {
        return dtlServiceClient.updateItem(itemDTO);
    }

    public void deleteItem(UUID id) {
        dtlServiceClient.deleteItem(id);
    }

    public List<ItemDTO> searchItems(String name, String owner, Type type) {
        return dtlServiceClient.searchItems(name, owner, type);
    }

}

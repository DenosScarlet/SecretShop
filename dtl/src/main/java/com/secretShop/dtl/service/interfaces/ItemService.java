package com.secretShop.dtl.service.interfaces;

import com.secretShop.dtl.enums.Type;
import com.secretShop.dtl.DTO.ItemDTO;


import java.util.List;
import java.util.UUID;

public interface ItemService {
    List<ItemDTO> findAll();

    ItemDTO findById(UUID id);

    ItemDTO save(ItemDTO item);

    void deleteById(UUID id);

    List<ItemDTO> searchItems(String name, String owner, Type type);
}

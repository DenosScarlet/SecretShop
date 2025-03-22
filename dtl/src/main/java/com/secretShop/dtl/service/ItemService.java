package com.secretShop.dtl.service;

import com.secretShop.dtl.service.dto.ItemDTO;

import java.util.List;
import java.util.UUID;

public interface ItemService {
    List<ItemDTO> findAll ();
    ItemDTO findById(UUID id);
    ItemDTO save (ItemDTO item);
    void deleteById (UUID id);
}

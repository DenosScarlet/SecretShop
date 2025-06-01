package com.secretShop.dtl.service.convertor;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.DTO.ItemDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item dtoToModel(ItemDTO itemDTO);

    ItemDTO modelToDto(Item item);

    List<ItemDTO> toListDto(List<Item> items);
}

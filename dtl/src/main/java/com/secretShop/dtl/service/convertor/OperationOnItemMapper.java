package com.secretShop.dtl.service.convertor;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.entity.OperationOnItem;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.DTO.OperationOnItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OperationOnItemMapper {

    @Mapping(target = "user", source = "userId", qualifiedByName = "uuidToUser")
    @Mapping(target = "item", source = "itemId", qualifiedByName = "uuidToItem")
    OperationOnItem dtoToModel(OperationOnItemDTO operationOnItemDTO);

    @Mapping(target = "userId", source = "user.userId")  // Маппинг из user.userId
    @Mapping(target = "itemId", source = "item.itemId")
    @Mapping(target = "operationHistory", source = "operationHistory")
    OperationOnItemDTO modelToDto(OperationOnItem operationOnItem);

    List<OperationOnItemDTO> toListDto(List<OperationOnItem> operationOnItems);

    @Named("uuidToUser")
    default User uuidToUser(UUID userId) {
        if (userId == null) return null;
        User user = new User();
        user.setUserId(userId);
        return user;
    }

    @Named("uuidToItem")
    default Item uuidToItem(UUID itemId) {
        if (itemId == null) return null;
        Item item = new Item();
        item.setItemId(itemId);
        return item;
    }
}
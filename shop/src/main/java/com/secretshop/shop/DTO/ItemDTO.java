package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.TypeProduct;
import lombok.Data;

import java.util.UUID;

@Data
public class ItemDTO {
    private UUID item_id;
    private String itemName;
    private String description;
    private String owner;
    private TypeProduct typeProduct;
    private Integer cost;
    private Integer count;
}

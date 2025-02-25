package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.TypeProduct;
import lombok.Data;

@Data
public class ProductDTO {
    private String productName;
    private String description;
    private String owner;
    private TypeProduct typeProduct;
    private Integer cost;
    private Integer count;
}

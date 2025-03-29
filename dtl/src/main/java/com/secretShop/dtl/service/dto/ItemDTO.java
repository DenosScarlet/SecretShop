package com.secretShop.dtl.service.dto;


import com.secretShop.dtl.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private UUID item_id;
    private String item_name;
    private String description;
    private String owner;
    private Type type;
    private Integer cost;
    private Integer count;
}

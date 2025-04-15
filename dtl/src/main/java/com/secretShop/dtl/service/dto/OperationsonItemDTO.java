package com.secretShop.dtl.service.dto;

import com.secretShop.dtl.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationsonItemDTO {
    private UUID operations_id;
    private UUID users_id;
    private UUID item_id;
    private Status status;
    private String operations_history;
}

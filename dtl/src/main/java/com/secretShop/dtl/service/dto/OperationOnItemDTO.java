package com.secretShop.dtl.service.dto;

import com.secretShop.dtl.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationOnItemDTO {
    private UUID operationsId;
    private UUID usersId;
    private UUID itemId;
    private Status status;
    private String operationsHistory;
}

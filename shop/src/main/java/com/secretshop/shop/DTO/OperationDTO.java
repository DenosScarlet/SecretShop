package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.Status;
import lombok.Data;

import java.util.UUID;

@Data
public class OperationDTO {
    private UUID operationsId;
    private UUID userId;
    private UUID itemId;
    private Status status;
    private String operationHistory;
}

package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationOnItemDTO {
    private UUID operationsId;
    private UUID userId;
    private UUID itemId;
    private Status status;
    private String operationHistory;
}

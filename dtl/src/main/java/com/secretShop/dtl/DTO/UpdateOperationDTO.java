package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.Status;
import lombok.Data;

@Data
public class UpdateOperationDTO {
    private Status status;
    private String operationHistory;
}
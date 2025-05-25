package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.Status;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateOperationDTO {
    private Status status;
    private String operationHistory;
}

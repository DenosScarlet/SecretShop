package com.secretShop.dtl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestRequest {
    private String operation; // FIND_ALL, FIND_BY_ID, COST_BY_ID, STATUS_BY_ID, GET_STEPS, UPDATE_STEPS, GET_BALANCE, UPDATE_BALANCE
    private Object payload;   // UUID, DTO или null
    private String correlationId;
}

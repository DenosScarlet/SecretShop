package com.secretShop.questMenu.DTO;

import com.secretShop.questMenu.enums.WorkGroup;
import lombok.Data;

@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String middleName;
    private WorkGroup workGroup;
    private Integer balance;
}

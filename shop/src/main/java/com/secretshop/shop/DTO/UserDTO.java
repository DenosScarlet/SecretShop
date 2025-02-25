package com.secretshop.shop.DTO;

import com.secretshop.shop.enums.WorkGroup;
import lombok.Data;

@Data
public class UserDTO {

    private String firstName;
    private String lastName;
    private String midleName;
    private WorkGroup workGroup;
    private Integer balance;

}

package com.secretshop.keycloak.DTO;

import com.secretshop.keycloak.enums.WorkGroup;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String middleName;
    private WorkGroup workGroup;
}

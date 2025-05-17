package com.secretShop.dtl.DTO;

import com.secretShop.dtl.enums.WorkGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private String avatar;
    private String firstName;
    private String lastName;
    private String middleName;
    private WorkGroup workGroup;
    private Integer balance;
}

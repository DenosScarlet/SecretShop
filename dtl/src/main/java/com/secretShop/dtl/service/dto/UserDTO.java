package com.secretShop.dtl.service.dto;

import com.secretShop.dtl.enums.WorkGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID user_id;
    private String avatar;
    private String firstName;
    private String lastName;
    private String middleName;
    private WorkGroup work_group;
    private Integer balance;
}

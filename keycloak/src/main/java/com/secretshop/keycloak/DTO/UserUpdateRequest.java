package com.secretshop.keycloak.DTO;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String avatar;
}

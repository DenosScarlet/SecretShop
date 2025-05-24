package com.secretshop.keycloak.DTO;

import com.secretshop.keycloak.enums.WorkGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FullUserCreateRequest {
    @NotBlank
    private String username;
    @Email
    private String email;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank private String password;
    private String avatar;
    private String middleName;
    private WorkGroup workGroup;
    private Integer balance = 0;
}

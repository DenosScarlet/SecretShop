package com.secretShop.dtl.service;


import com.secretShop.dtl.service.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> findAll ();
    UserDTO findById(UUID id);
    UserDTO save (UserDTO user);
    void deleteById (UUID id);
}

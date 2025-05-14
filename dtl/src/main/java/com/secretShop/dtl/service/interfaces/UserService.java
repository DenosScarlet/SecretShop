package com.secretShop.dtl.service.interfaces;


import com.secretShop.dtl.DTO.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> findAll ();
    UserDTO findById(UUID id);
    UserDTO save (UserDTO user);
    void deleteById (UUID id);
}

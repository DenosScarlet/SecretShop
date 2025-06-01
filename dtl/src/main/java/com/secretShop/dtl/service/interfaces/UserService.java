package com.secretShop.dtl.service.interfaces;

import com.secretShop.dtl.DTO.UpdateBalanceDTO;
import com.secretShop.dtl.DTO.UserDTO;
import com.secretShop.dtl.enums.WorkGroup;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDTO> findAll();
    UserDTO findById(UUID id);
    UserDTO save(UserDTO user);
    UserDTO create(UserDTO user);
    void deleteById(UUID id);
    void updateBalance(UpdateBalanceDTO balanceDTO);
    List<UserDTO> findByWorkGroup(String workGroup);
}
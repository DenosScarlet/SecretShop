package com.secretShop.dtl.service.convertor;


import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.service.dto.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User dtoToModel(UserDTO userDTO);

    UserDTO modelToDto(User user);

    List<UserDTO> toListDto(List<User> users);
}

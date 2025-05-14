package com.secretShop.dtl.service.impl;

import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.service.interfaces.UserService;
import com.secretShop.dtl.service.convertor.UserMapper;
import com.secretShop.dtl.DTO.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public List<UserDTO> findAll() { return userMapper.toListDto(userRepository.findAll()); }

    @Override
    public UserDTO findById(UUID id) { return Optional.of(getById(id)).map(userMapper::modelToDto).get(); }

    @Override
    public UserDTO save(UserDTO user) {
        return userMapper.modelToDto(userRepository.save(
                userMapper.dtoToModel(user)));
    }

    @Override
    public void deleteById(UUID id) {
        var book = getById(id);
        userRepository.delete(book);
    }

    private User getById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Пользователь с id: " + id + " не найден :("));

    }
}

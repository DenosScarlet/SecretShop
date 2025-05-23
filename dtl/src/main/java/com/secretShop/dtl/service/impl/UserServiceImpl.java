package com.secretShop.dtl.service.impl;

import com.secretShop.dtl.DTO.UpdateBalanceDTO;
import com.secretShop.dtl.DTO.UserDTO;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.enums.WorkGroup;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.service.interfaces.UserService;
import com.secretShop.dtl.service.convertor.UserMapper;
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
    public List<UserDTO> findAll() {
        return userMapper.toListDto(userRepository.findAll());
    }

    @Override
    public UserDTO findById(UUID id) {
        return Optional.of(getById(id)).map(userMapper::modelToDto).get();
    }

    @Override
    @Transactional
    public UserDTO create(UserDTO userDTO) {
        if (userDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (userRepository.existsById(userDTO.getUserId())) {
            throw new IllegalStateException("User with ID " + userDTO.getUserId() + " already exists");
        }
        User user = userMapper.dtoToModel(userDTO);
        User savedUser = userRepository.save(user); // save с новым ID работает корректно
        return userMapper.modelToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {
        if (!userRepository.existsById(userDTO.getUserId())) {
            throw new IllegalStateException("User not found");
        }
        User user = userMapper.dtoToModel(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.modelToDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        var user = getById(id);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void updateBalance(UpdateBalanceDTO balanceDTO) {
        userRepository.updateBalanceById(balanceDTO.getUserId(), balanceDTO.getNewBalance());
    }

    @Override
    public List<UserDTO> findByWorkGroup(String workGroup) {
        try {
            WorkGroup group = WorkGroup.valueOf(workGroup.toUpperCase());
            return userMapper.toListDto(userRepository.findUsersByWorkGroup(group));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid work group: " + workGroup);
        }
    }

    private User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Пользователь с id: " + id + " не найден :("));
    }
}
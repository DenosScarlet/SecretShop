package com.secretshop.keycloak.service.impl;

import com.secretshop.keycloak.DTO.UserDTO;
import com.secretshop.keycloak.DTO.BalanceUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserEventClient {
    UserDTO createUser(UserDTO userDTO);
    UserDTO createOrUpdateUser(UserDTO userDTO);
    UserDTO getUser(UUID userId);
    List<UserDTO> getAllUsers(int page, int size);
    void sendUserCreatedEvent(UserDTO userDTO);
    void sendUserUpdatedEvent(UserDTO userDTO);
    void sendUserDeletedEvent(UUID userId);
    void updateBalance(UUID userId, int newBalance);
    String uploadAvatar(MultipartFile file, String fileName, String bucketName);

    byte[] downloadFile(String fileName, String bucketName);
}
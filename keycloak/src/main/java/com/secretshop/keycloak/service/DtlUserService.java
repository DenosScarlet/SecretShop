package com.secretshop.keycloak.service;

import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.enums.WorkGroup;
import com.secretshop.keycloak.exception.DtlUserNotFoundException;
import com.secretshop.keycloak.service.impl.UserEventClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DtlUserService {

    private final UserEventClient userEventClient;
    private final KeycloakUserService keycloakUserService;

    public UserDTO getUser(UUID userId) {
        try {
            return userEventClient.getUser(userId);
        } catch (Exception e) {
            log.error("Failed to get user from DTL", e);
            throw new DtlUserNotFoundException("User not found in DTL");
        }
    }

    public List<UserDTO> getAllUsers(int page, int size) {
        return userEventClient.getAllUsers(page, size);
    }

    public UserDTO updateUser(UUID userId, UserUpdateDTO updateDTO) {
        UserDTO userDTO = getUser(userId);

        if (updateDTO.getFirstName() != null) userDTO.setFirstName(updateDTO.getFirstName());
        if (updateDTO.getLastName() != null) userDTO.setLastName(updateDTO.getLastName());
        if (updateDTO.getMiddleName() != null) userDTO.setMiddleName(updateDTO.getMiddleName());
        if (updateDTO.getWorkGroup() != null) userDTO.setWorkGroup(updateDTO.getWorkGroup());

        userEventClient.sendUserUpdatedEvent(userDTO);
        return userDTO;
    }

    public UserDTO updateBalance(UUID userId, BalanceUpdateDTO balanceUpdate) {
        UserDTO userDTO = getUser(userId);
        userEventClient.updateBalance(userId, balanceUpdate.getNewBalance());
        userDTO.setBalance(balanceUpdate.getNewBalance());
        return userDTO;
    }


    public String getUserAvatar(UUID userId) {
        return getUser(userId).getAvatar();
    }

    public UserDTO updateAvatar(UUID userId, AvatarUpdateDTO avatarUpdate) {
        UserDTO userDTO = getUser(userId);
        userDTO.setAvatar(avatarUpdate.getAvatarUrl());
        userEventClient.sendUserUpdatedEvent(userDTO);
        return userDTO;
    }

    public WorkGroup getUserWorkGroup(UUID userId) {
        return getUser(userId).getWorkGroup();
    }
}
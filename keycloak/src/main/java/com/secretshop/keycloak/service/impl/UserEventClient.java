package com.secretshop.keycloak.service.impl;

import com.secretshop.keycloak.DTO.UserDTO;

import java.util.UUID;

public interface UserEventClient {
    void sendUserCreatedEvent(UserDTO userDTO);
    void sendUserUpdatedEvent(UserDTO userDTO);
    void sendUserDeletedEvent(UUID userId);

    UserDTO createUser(UserDTO userDTO);
}

package com.secretshop.keycloak.service;

import com.secretshop.keycloak.DTO.DtlProperties;
import com.secretshop.keycloak.DTO.UserDTO;
import com.secretshop.keycloak.service.impl.UserEventClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class RestUserEventClient implements UserEventClient {

    private final RestTemplate restTemplate;
    private final DtlProperties dtlProperties;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        String url = dtlProperties.getBaseUrl() + "/api/users";
        log.info("Sending POST to {} with user: {}", url, userDTO);
        ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, userDTO, UserDTO.class);
        log.info("Received response: {}", response);
        return response.getBody();
    }

    @Override
    public void sendUserCreatedEvent(UserDTO userDTO) {
        String url = dtlProperties.getBaseUrl() + "/api/users";
        restTemplate.postForEntity(url, userDTO, Void.class);
    }

    @Override
    public void sendUserUpdatedEvent(UserDTO userDTO) {
        String url = dtlProperties.getBaseUrl() + "/api/users/" + userDTO.getUserId();
        restTemplate.put(url, userDTO);
    }

    @Override
    public void sendUserDeletedEvent(UUID userId) {
        String url = dtlProperties.getBaseUrl() + "/api/users/" + userId;
        restTemplate.delete(url);
    }
}

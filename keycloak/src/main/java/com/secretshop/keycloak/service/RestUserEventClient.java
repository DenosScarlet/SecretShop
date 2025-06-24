package com.secretshop.keycloak.service;

import com.secretshop.keycloak.DTO.BalanceUpdateDTO;
import com.secretshop.keycloak.DTO.DtlProperties;
import com.secretshop.keycloak.DTO.UserDTO;
import com.secretshop.keycloak.service.impl.UserEventClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

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
        log.info("Sending PUT to {} with user: {}", url, userDTO);
        restTemplate.put(url, userDTO);
    }

    @Override
    public void sendUserDeletedEvent(UUID userId) {
        String url = dtlProperties.getBaseUrl() + "/api/users/" + userId;
        log.info("Sending DELETE to {}", url);
        restTemplate.delete(url);
    }

    @Override
    public UserDTO createOrUpdateUser(UserDTO userDTO) {
        String url = dtlProperties.getBaseUrl() + "/api/users/sync";
        log.info("Sending PUT to {} with user: {}", url, userDTO);
        ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, userDTO, UserDTO.class);
        log.info("Received response: {}", response);
        return response.getBody();
    }

    @Override
    public UserDTO getUser(UUID userId) {
        String url = dtlProperties.getBaseUrl() + "/api/users/" + userId;
        log.info("Sending GET to {}", url);
        ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
        log.info("Received response: {}", response);
        return response.getBody();
    }

    @Override
    public List<UserDTO> getAllUsers(int page, int size) {
        String url = String.format("%s/api/users?page=%d&size=%d", dtlProperties.getBaseUrl(), page, size);
        log.info("Sending GET to {}", url);
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        log.info("Received response: {}", response);
        return response.getBody();
    }

    @Override
    public void updateBalance(UUID userId, int newBalance) {
        String url = dtlProperties.getBaseUrl() + "/api/users/balance";
        BalanceUpdateDTO balanceDTO = new BalanceUpdateDTO(userId, newBalance);
        log.info("Sending POST to {} with balance: {}", url, balanceDTO);
        try {
            restTemplate.postForObject(url, balanceDTO, Void.class);
            log.info("Balance updated for user {}", userId);
        } catch (Exception e) {
            log.error("Failed to update balance for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to update balance", e);
        }
    }

    @Override
    public String uploadAvatar(MultipartFile file, String fileName, String bucketName) {
        String url = dtlProperties.getBaseUrl() + "/api/files/upload-with-name";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            byte[] fileContent = file.getBytes();
            body.add("file", new MultipartByteArrayResource(fileContent, file.getOriginalFilename()));
            body.add("fileName", fileName);
            body.add("bucketName", bucketName);
        } catch (IOException e) {
            log.error("Ошибка чтения файла: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка чтения файла", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getBody();
    }

    public byte[] downloadFile(String fileName, String bucketName) {
        String url = dtlProperties.getBaseUrl() + "/api/files/download?fileName=" + fileName + "&bucketName=" + bucketName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, requestEntity, byte[].class);
        return response.getBody();
    }

    // Остальные методы остаются без изменений...

    static class MultipartByteArrayResource extends ByteArrayResource {
        private final String filename;

        public MultipartByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }
}
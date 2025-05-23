package com.secretshop.keycloak.controller;

import com.secretshop.keycloak.DTO.FullUserCreateRequest;
import com.secretshop.keycloak.DTO.KeycloakUserResponse;
import com.secretshop.keycloak.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserIntegrationController {
    private final KeycloakUserService keycloakUserService;

    @PostMapping("/full")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KeycloakUserResponse> createFullUser(
            @RequestBody FullUserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(keycloakUserService.createUser(request));
    }
}

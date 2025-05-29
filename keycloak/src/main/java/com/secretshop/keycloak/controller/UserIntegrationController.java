package com.secretshop.keycloak.controller;

import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // Get user by ID
    @GetMapping("/{userId}")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')") // Only admin can access user details
    public ResponseEntity<org.keycloak.representations.idm.UserRepresentation> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(keycloakUserService.getUserById(userId.toString()));
    }

    // Search users by username or email
    @GetMapping("/search")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<List<UserRepresentation>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(keycloakUserService.searchUsers(username, email));
    }

    // Update user
    @PutMapping("/{userId}")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<KeycloakUserResponse> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(keycloakUserService.updateUser(userId.toString(), request));
    }

    // Disable user
    @PutMapping("/{userId}/disable")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<Void> disableUser(@PathVariable UUID userId) {
        keycloakUserService.disableUser(userId.toString());
        return ResponseEntity.noContent().build();
    }

    // Enable user
    @PutMapping("/{userId}/enable")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<Void> enableUser(@PathVariable UUID userId) {
        keycloakUserService.enableUser(userId.toString());
        return ResponseEntity.noContent().build();
    }

    // Reset password
    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<Void> resetPassword(
            @PathVariable UUID userId,
            @RequestBody PasswordResetRequest request) {
        keycloakUserService.resetPassword(userId.toString(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    // Get user roles
    @GetMapping("/{userId}/roles")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable UUID userId) {
        return ResponseEntity.ok(keycloakUserService.getUserRoles(userId.toString()));
    }

    // Assign role to user
    @PostMapping("/{userId}/roles")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID userId,
            @RequestBody RoleAssignmentRequest request) {
        keycloakUserService.assignRole(userId.toString(), request.getRoleName());
        return ResponseEntity.noContent().build();
    }

    // Remove role from user
    @DeleteMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<Void> removeRole(
            @PathVariable UUID userId,
            @PathVariable String roleName) {
        keycloakUserService.removeRole(userId.toString(), roleName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auth-info")
    public Map<String, Object> authInfo(@AuthenticationPrincipal Jwt jwt) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return Map.of(
                "authenticated", auth.isAuthenticated(),
                "principal", auth.getPrincipal(),
                "authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),
                "jwtClaims", jwt.getClaims()
        );
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        keycloakUserService.deleteUser(userId);
    }

    // Get all users with pagination
    @GetMapping
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<List<UserRepresentation>> getAllUsers(
            @RequestParam(defaultValue = "0") int first,
            @RequestParam(defaultValue = "20") int max) {
        return ResponseEntity.ok(keycloakUserService.getAllUsers(first, max));
    }

    // Get user by username (для совместимости с frontend)
    @GetMapping("/username/{username}")
    @PreAuthorize("@securityUtils.hasRealmRole('admin')")
    public ResponseEntity<UserRepresentation> getUserByUsername(@PathVariable String username) {
        List<UserRepresentation> users = keycloakUserService.searchUsers(username, null);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users.get(0));
    }

}

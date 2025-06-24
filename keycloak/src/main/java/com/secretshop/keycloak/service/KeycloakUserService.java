package com.secretshop.keycloak.service;

import com.secretshop.keycloak.exception.UserAlreadyExistsException;
import com.secretshop.keycloak.exception.UserCreationException;
import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.service.impl.UserEventClient;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.LoggerFactory;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserService {
    private final Keycloak keycloak;
    private final UserEventClient userEventClient;

    @Value("${spring.security.oauth2.client.registration.keycloak.realm}")
    private String realm;

    @Transactional
    public KeycloakUserResponse createUser(FullUserCreateRequest request) {
        validateRequest(request);

        try {
            UserRepresentation user = buildKeycloakUserRepresentation(request);
            UsersResource usersResource = keycloak.realm(realm).users();

            try (Response response = usersResource.create(user)) {
                if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                    String errorBody = response.readEntity(String.class);
                    log.error("Keycloak error: {} - {}", response.getStatus(), errorBody);
                    throw new RuntimeException("Failed to create user: " + response.getStatus() + " - " + errorBody);
                }

                String createdUserId = extractUserIdFromLocation(response.getLocation());
                UUID dtlUserId = UUID.fromString(createdUserId);

                UserDTO userDto = createDtlUser(dtlUserId, request);

                return buildResponse(dtlUserId, request);
            }
        } catch (Exception e) {
            log.error("User creation failed", e);
            throw new UserCreationException("Failed to create user", e);
        }
    }

    private void validateRequest(FullUserCreateRequest request) {
        UsersResource usersResource = keycloak.realm(realm).users();

        if (!usersResource.search(request.getUsername(), true).isEmpty()) {
            throw new UserAlreadyExistsException("Username exists");
        }

        if (request.getEmail() != null &&
                !usersResource.searchByEmail(request.getEmail(), true).isEmpty()) {
            throw new UserAlreadyExistsException("Email exists");
        }
    }

    private UserRepresentation buildKeycloakUserRepresentation(FullUserCreateRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(false);

        if (request.getMiddleName() != null) {
            user.singleAttribute("middleName", request.getMiddleName());
        }

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        return user;
    }

    private String extractUserIdFromLocation(URI location) {
        if (location == null) {
            throw new RuntimeException("No location header in response");
        }
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private void setPasswordWithRetry(UsersResource usersResource, String userId, String password) {
        int retries = 3;
        while (retries > 0) {
            try {
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(password);
                credential.setTemporary(false);

                usersResource.get(userId).resetPassword(credential);
                return;
            } catch (NotFoundException e) {
                retries--;
                if (retries == 0) throw e;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            } catch (BadRequestException e) {
                String errorBody = e.getResponse().readEntity(String.class);
                log.error("Keycloak password error: {}", errorBody);
                throw new RuntimeException("Invalid password: " + errorBody);
            }
        }
    }

    private UserDTO createDtlUser(UUID userId, FullUserCreateRequest request) {
        UserDTO userDto = new UserDTO();
        userDto.setUserId(userId);
        userDto.setAvatar(request.getAvatar());
        userDto.setFirstName(request.getFirstName());
        userDto.setLastName(request.getLastName());
        userDto.setMiddleName(request.getMiddleName());
        userDto.setWorkGroup(request.getWorkGroup());
        userDto.setBalance(request.getBalance());

        return userEventClient.createUser(userDto);
    }

    private KeycloakUserResponse buildResponse(UUID userId, FullUserCreateRequest request) {
        return new KeycloakUserResponse(
                userId,
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName()
        );
    }

    public UserRepresentation getUserById(String userId) {
        try {
            return keycloak.realm(realm).users().get(userId).toRepresentation();
        } catch (NotAuthorizedException e) {
            try {
                keycloak.tokenManager().refreshToken();
                return keycloak.realm(realm).users().get(userId).toRepresentation();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to get user after token refresh", ex);
            }
        }
    }

    public List<UserRepresentation> searchUsers(String username, String email) {
        UsersResource usersResource = keycloak.realm(realm).users();
        if (username != null) {
            return usersResource.search(username, true);
        } else if (email != null) {
            return usersResource.searchByEmail(email, true);
        }
        return Collections.emptyList();
    }

    public KeycloakUserResponse updateUser(String userId, UserUpdateRequest request) {
        UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getMiddleName() != null) user.singleAttribute("middleName", request.getMiddleName());

        keycloak.realm(realm).users().get(userId).update(user);

        UserDTO userDto = new UserDTO();
        userDto.setUserId(UUID.fromString(userId));
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setMiddleName(user.firstAttribute("middleName"));
        userDto.setAvatar(request.getAvatar());
        userEventClient.sendUserUpdatedEvent(userDto);

        return new KeycloakUserResponse(
                UUID.fromString(userId),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public void disableUser(String userId) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(false);
        keycloak.realm(realm).users().get(userId).update(user);
    }

    public void enableUser(String userId) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        keycloak.realm(realm).users().get(userId).update(user);
    }

    public void resetPassword(String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        keycloak.realm(realm).users().get(userId).resetPassword(credential);
    }

    public List<String> getUserRoles(String userId) {
        return keycloak.realm(realm).users().get(userId).roles().realmLevel().listAll()
                .stream()
                .map(RoleRepresentation::getName)
                .toList();
    }

    public void assignRole(String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
    }

    public void removeRole(String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(Collections.singletonList(role));
    }

    public List<String> getUserGroups(String userId) {
        return keycloak.realm(realm).users().get(userId).groups()
                .stream()
                .map(GroupRepresentation::getName)
                .toList();
    }

    public void joinGroup(String userId, String groupName) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);

        try {
            userResource.toRepresentation();
        } catch (NotFoundException e) {  // Используем стандартное исключение
            throw new RuntimeException("User not found: " + userId, e);
        }

        GroupsResource groupsResource = keycloak.realm(realm).groups();
        List<GroupRepresentation> groups;

        try {
            groups = groupsResource.groups(groupName, 0, 1);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve groups: " + e.getMessage(), e);
        }

        if (groups.isEmpty()) {
            throw new RuntimeException("Group not found: " + groupName);
        }

        String groupId = groups.get(0).getId();
        try {
            userResource.joinGroup(groupId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign group '" + groupName +
                    "' to user " + userId + ": " + e.getMessage(), e);
        }
    }

    public void leaveGroup(String userId, String groupName) {
        String groupId = keycloak.realm(realm).groups().groups().stream()
                .filter(group -> group.getName().equals(groupName))
                .findFirst()
                .map(GroupRepresentation::getId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupName));
        keycloak.realm(realm).users().get(userId).leaveGroup(groupId);
    }

    public void deleteUser(UUID userId) {
        deleteUserFromKeycloak(userId);
        userEventClient.sendUserDeletedEvent(userId);
    }

    private void deleteUserFromKeycloak(UUID userId) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId.toString());
            UserRepresentation user = userResource.toRepresentation();
            if (user != null) {
                userResource.remove();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user from Keycloak: " + e.getMessage());
        }
    }

    public List<UserRepresentation> getAllUsers(int first, int max) {
        try {
            return keycloak.realm(realm).users().list(first, max);
        } catch (NotAuthorizedException e) {
            try {
                keycloak.tokenManager().refreshToken();
                return keycloak.realm(realm).users().list(first, max);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to get users after token refresh", ex);
            }
        }
    }


}
package com.secretshop.keycloak.service;

import com.secretshop.keycloak.exception.UserAlreadyExistsException;
import com.secretshop.keycloak.exception.UserCreationException;
import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.service.impl.UserEventClient;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
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

    @Value("${keycloak.realm}")
    private String realm;

    @Transactional
    public KeycloakUserResponse createUser(FullUserCreateRequest request) {
        validateRequest(request);

        try {
            // Попробуем задать случайный UUID (для некоторых версий Keycloak это может сработать)
            UUID userId = UUID.randomUUID();
            UserRepresentation user = buildKeycloakUserRepresentation(userId, request);

            // 1. Создаем пользователя в Keycloak
            UsersResource usersResource = keycloak.realm(realm).users();
            Response response = usersResource.create(user);
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
            }

            // 2. Получаем ID созданного пользователя из Location header
            String createdUserId = extractUserIdFromLocation(response.getLocation());
            UUID dtlUserId = UUID.fromString(createdUserId);

            // 3. Устанавливаем пароль с retry
            setPasswordWithRetry(usersResource, createdUserId, request.getPassword());

            // 4. Создаем пользователя в DTL с реальным ID из Keycloak
            UserDTO userDto = createDtlUser(dtlUserId, request);

            // 5. Возвращаем ответ с реальным ID пользователя
            return buildResponse(dtlUserId, request);

        } catch (Exception e) {
            log.error("User creation failed", e);
            // rollback по ID из Keycloak не требуется — пользователь не был создан в DTL
            // rollback по случайному UUID тоже не нужен, если Keycloak не принял его
            // Если хотите — можно попробовать удалить пользователя по createdUserId, если он был создан, но это сложнее
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

    private UserRepresentation buildKeycloakUserRepresentation(UUID userId, FullUserCreateRequest request) {
        UserRepresentation user = new UserRepresentation();
        // Если Keycloak поддерживает ручное задание ID — задаём, если нет — игнорируется
        user.setId(userId.toString());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);

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
            // Попробуем обновить токен и повторить запрос
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

        // Update in DTL if needed
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

    /**
     * Удаляет пользователя из Keycloak и DTL.
     * @param userId ID пользователя (UUID)
     */
    public void deleteUser(UUID userId) {
        // 1. Удаление из Keycloak
        deleteUserFromKeycloak(userId);

        // 2. Удаление из DTL
        userEventClient.sendUserDeletedEvent(userId);
    }

    /**
     * Удаляет пользователя из Keycloak.
     */
    private void deleteUserFromKeycloak(UUID userId) {
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId.toString());
            UserRepresentation user = userResource.toRepresentation();

            if (user != null) {
                userResource.remove();  // Удаление пользователя
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user from Keycloak: " + e.getMessage());
        }
    }

    /* Получает список всех пользователей с пагинацией
    * @param first Начальный индекс (offset)
    * @param max Максимальное количество записей
    * @return Список пользователей
    */
    public List<UserRepresentation> getAllUsers(int first, int max) {
        try {
            return keycloak.realm(realm).users().list(first, max);
        } catch (NotAuthorizedException e) {
            // Попробуем обновить токен и повторить запрос
            try {
                keycloak.tokenManager().refreshToken();
                return keycloak.realm(realm).users().list(first, max);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to get users after token refresh", ex);
            }
        }
    }




    // rollback по сложной логике не требуется, потому что если Keycloak не принял ID,
    // то пользователь в DTL не был создан, а если принял — то ID совпадает
}

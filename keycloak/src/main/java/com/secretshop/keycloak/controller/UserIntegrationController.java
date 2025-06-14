package com.secretshop.keycloak.controller;

import com.secretshop.keycloak.DTO.*;
import com.secretshop.keycloak.service.KeycloakUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "User Integration", description = "Операции для управления пользователями через Keycloak")
public class UserIntegrationController {

    private final KeycloakUserService keycloakUserService;

    @Operation(summary = "Создать пользователя (полные данные)", description = "Создаёт нового пользователя с полным набором данных")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    })
    @PostMapping("/full")
    public ResponseEntity<KeycloakUserResponse> createFullUser(
            @Parameter(description = "Данные для создания пользователя", required = true)
            @RequestBody FullUserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(keycloakUserService.createUser(request));
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает информацию о пользователе по его UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserRepresentation> getUserById(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(keycloakUserService.getUserById(userId.toString()));
    }

    @Operation(summary = "Поиск пользователей", description = "Ищет пользователей по username или email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей")
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserRepresentation>> searchUsers(
            @Parameter(description = "Имя пользователя") @RequestParam(required = false) String username,
            @Parameter(description = "Email пользователя") @RequestParam(required = false) String email) {
        return ResponseEntity.ok(keycloakUserService.searchUsers(username, email));
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<KeycloakUserResponse> updateUser(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Данные для обновления пользователя", required = true) @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(keycloakUserService.updateUser(userId.toString(), request));
    }

    @Operation(summary = "Деактивировать пользователя", description = "Деактивирует пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь деактивирован")
    })
    @PutMapping("/{userId}/disable")
    public ResponseEntity<Void> disableUser(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        keycloakUserService.disableUser(userId.toString());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Активировать пользователя", description = "Активирует пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь активирован")
    })
    @PutMapping("/{userId}/enable")
    public ResponseEntity<Void> enableUser(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        keycloakUserService.enableUser(userId.toString());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Сбросить пароль пользователя", description = "Сбрасывает пароль пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пароль сброшен")
    })
    @PutMapping("/{userId}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Запрос на сброс пароля", required = true) @RequestBody PasswordResetRequest request) {
        keycloakUserService.resetPassword(userId.toString(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить роли пользователя", description = "Возвращает список ролей пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список ролей пользователя")
    })
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<String>> getUserRoles(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(keycloakUserService.getUserRoles(userId.toString()));
    }

    @Operation(summary = "Назначить роль пользователю", description = "Назначает роль пользователю по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Роль назначена")
    })
    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> assignRole(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Запрос на назначение роли", required = true) @RequestBody RoleAssignmentRequest request) {
        keycloakUserService.assignRole(userId.toString(), request.getRoleName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить роль у пользователя", description = "Удаляет роль у пользователя по UUID и названию роли")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Роль удалена")
    })
    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRole(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Название роли", required = true) @PathVariable String roleName) {
        keycloakUserService.removeRole(userId.toString(), roleName);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить группы пользователя", description = "Возвращает список групп пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список групп пользователя")
    })
    @GetMapping("/{userId}/groups")
    public ResponseEntity<List<String>> getUserGroups(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        return ResponseEntity.ok(keycloakUserService.getUserGroups(userId.toString()));
    }

    @Operation(summary = "Назначить группу пользователю", description = "Назначает группу пользователю по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Группа назначена")
    })
    @PostMapping("/{userId}/groups")
    public ResponseEntity<Void> assignGroup(
            @Parameter(description = "UUID пользователя", required = true)
            @PathVariable UUID userId,

            @Parameter(description = "Запрос на назначение группы", required = true)
            @RequestBody GroupAssignmentRequest request) {

        keycloakUserService.joinGroup(userId.toString(), request.getGroupName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить группу у пользователя", description = "Удаляет группу у пользователя по UUID и названию группы")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Группа удалена")
    })
    @DeleteMapping("/{userId}/groups/{groupName}")
    public ResponseEntity<Void> removeGroup(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId,
            @Parameter(description = "Название группы", required = true) @PathVariable String groupName) {
        keycloakUserService.leaveGroup(userId.toString(), groupName);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить информацию об аутентификации", description = "Возвращает информацию о текущем пользователе и его токене")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о пользователе возвращена")
    })
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

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID пользователя", required = true) @PathVariable UUID userId) {
        keycloakUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить всех пользователей (с пагинацией)", description = "Возвращает список всех пользователей с поддержкой пагинации")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей")
    })
    @GetMapping
    public ResponseEntity<List<UserRepresentation>> getAllUsers(
            @Parameter(description = "Смещение (по умолчанию 0)") @RequestParam(defaultValue = "0") int first,
            @Parameter(description = "Максимальное количество (по умолчанию 20)") @RequestParam(defaultValue = "20") int max) {
        return ResponseEntity.ok(keycloakUserService.getAllUsers(first, max));
    }

    @Operation(summary = "Получить пользователя по username", description = "Возвращает пользователя по username (для совместимости с frontend)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserRepresentation> getUserByUsername(
            @Parameter(description = "Username пользователя", required = true) @PathVariable String username) {
        List<UserRepresentation> users = keycloakUserService.searchUsers(username, null);
        if (users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(users.get(0));
    }
}
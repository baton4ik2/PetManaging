package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akbirov.petproject.util.RoleUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API для управления пользователями")
public class UserController {

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        // Простая заглушка для совместимости с фронтендом
        // В реальном приложении здесь должна быть аутентификация и получение данных из БД
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", 1L);
        profile.put("username", "user");
        profile.put("email", "user@example.com");
        profile.put("roles", RoleUtils.removeRolePrefix(new String[]{"ROLE_USER"}));
        profile.put("enabled", true);
        profile.put("createdAt", LocalDateTime.now().minusDays(1).toString());
        profile.put("updatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(summary = "Обновить профиль текущего пользователя")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(@RequestBody UpdateProfileRequest request) {
        // Простая заглушка для совместимости с фронтендом
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", 1L);
        profile.put("username", "user");
        profile.put("email", request.getEmail() != null ? request.getEmail() : "user@example.com");
        profile.put("roles", RoleUtils.removeRolePrefix(new String[]{"ROLE_USER"}));
        profile.put("enabled", true);
        profile.put("createdAt", LocalDateTime.now().minusDays(1).toString());
        profile.put("updatedAt", LocalDateTime.now().toString());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request) {
        // Простая заглушка для совместимости с фронтендом
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    @Data
    public static class UpdateProfileRequest {
        private String email;
    }

    @Data
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }
}


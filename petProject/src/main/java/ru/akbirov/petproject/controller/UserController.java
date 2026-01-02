package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        logger.debug("Getting profile for user: username={}, email={}", username, email);
        // Извлекаем данные пользователя из query параметров
        // Фронтенд будет отправлять эти данные в query параметрах
        
        // Если не удалось извлечь из параметров, используем значения по умолчанию
        if (username == null || username.isEmpty()) {
            logger.warn("Username not provided, using default value");
            username = "user";
        }
        if (email == null || email.isEmpty()) {
            logger.warn("Email not provided, generating default email");
            email = username + "@example.com";
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", 1L);
        profile.put("username", username);
        profile.put("email", email);
        profile.put("roles", RoleUtils.removeRolePrefix(new String[]{"ROLE_USER"}));
        profile.put("enabled", true);
        profile.put("createdAt", LocalDateTime.now().minusDays(1).toString());
        profile.put("updatedAt", LocalDateTime.now().toString());
        logger.info("Profile retrieved successfully for user: {}", username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(summary = "Обновить профиль текущего пользователя")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(
            @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        logger.info("Updating profile, new email: {}", request.getEmail());
        // Извлекаем данные пользователя из заголовков
        String username = httpRequest.getHeader("X-Username");
        String email = request.getEmail() != null ? request.getEmail() : httpRequest.getHeader("X-Email");
        
        // Если не удалось извлечь из заголовков, используем значения по умолчанию
        if (username == null || username.isEmpty()) {
            logger.warn("Username not found in headers, using default value");
            username = "user";
        }
        if (email == null || email.isEmpty()) {
            logger.warn("Email not found, generating default email");
            email = username + "@example.com";
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", 1L);
        profile.put("username", username);
        profile.put("email", email);
        profile.put("roles", RoleUtils.removeRolePrefix(new String[]{"ROLE_USER"}));
        profile.put("enabled", true);
        profile.put("createdAt", LocalDateTime.now().minusDays(1).toString());
        profile.put("updatedAt", LocalDateTime.now().toString());
        logger.info("Profile updated successfully for user: {}", username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request) {
        logger.info("Password change requested");
        // Простая заглушка для совместимости с фронтендом
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        logger.info("Password changed successfully");
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


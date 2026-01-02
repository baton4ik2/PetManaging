package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akbirov.petproject.util.RoleUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API для аутентификации")
public class AuthController {

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        // Простая заглушка для совместимости с фронтендом
        Map<String, Object> response = new HashMap<>();
        response.put("token", "dummy-token-" + System.currentTimeMillis());
        response.put("username", request.getUsername());
        response.put("email", request.getEmail());
        response.put("roles", RoleUtils.removeRolePrefix(new String[]{"ROLE_USER"}));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        // Простая заглушка для совместимости с фронтендом
        Map<String, Object> response = new HashMap<>();
        response.put("token", "dummy-token-" + System.currentTimeMillis());
        response.put("username", request.getUsername());
        response.put("email", request.getUsername() + "@example.com");
        response.put("roles", RoleUtils.removeRolePrefix(new String[]{"ROLE_USER"}));
        return ResponseEntity.ok(response);
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}


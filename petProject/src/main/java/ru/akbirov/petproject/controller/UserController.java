package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akbirov.petproject.dto.ChangePasswordDto;
import ru.akbirov.petproject.dto.UpdateProfileDto;
import ru.akbirov.petproject.dto.UserProfileDto;
import ru.akbirov.petproject.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API для управления пользователями")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    public ResponseEntity<UserProfileDto> getCurrentUser(
            @RequestParam(required = false) String username) {
        logger.info("Getting profile for username: {}", username);
        
        // Если username не передан, возвращаем ошибку
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username not provided in query params");
            throw new ru.akbirov.petproject.exception.UserNotFoundException("Username is required");
        }
        
        UserProfileDto profile = userService.getCurrentUserProfile(username.trim());
        logger.info("Profile returned for user: username={}, email={}", 
                profile.getUsername(), profile.getEmail());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(summary = "Обновить профиль текущего пользователя")
    public ResponseEntity<UserProfileDto> updateCurrentUser(
            @RequestParam(required = false) String username,
            @Valid @RequestBody UpdateProfileDto updateDto) {
        logger.info("Updating profile for username: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username not provided in query params");
            throw new ru.akbirov.petproject.exception.UserNotFoundException("Username is required");
        }
        
        UserProfileDto profile = userService.updateUserProfile(username.trim(), updateDto);
        logger.info("Profile updated successfully: username={}, email={}", 
                profile.getUsername(), profile.getEmail());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam(required = false) String username,
            @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        logger.info("Password change requested for username: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Username not provided in query params");
            throw new ru.akbirov.petproject.exception.UserNotFoundException("Username is required");
        }
        
        userService.changePassword(username.trim(), 
                changePasswordDto.getCurrentPassword(), 
                changePasswordDto.getNewPassword());
        
        logger.info("Password changed successfully for username: {}", username);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }
}

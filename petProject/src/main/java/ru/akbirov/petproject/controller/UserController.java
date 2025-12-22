package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akbirov.petproject.dto.ChangePasswordDto;
import ru.akbirov.petproject.dto.UpdateProfileDto;
import ru.akbirov.petproject.dto.UserProfileDto;
import ru.akbirov.petproject.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "API для управления профилем пользователя")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    @Operation(summary = "Получить профиль текущего пользователя")
    public ResponseEntity<UserProfileDto> getCurrentUser() {
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/me")
    @Operation(summary = "Обновить профиль текущего пользователя")
    public ResponseEntity<UserProfileDto> updateProfile(@Valid @RequestBody UpdateProfileDto updateDto) {
        UserProfileDto updatedProfile = userService.updateProfile(updateDto);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @PutMapping("/me/password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
        return ResponseEntity.ok().build();
    }
}


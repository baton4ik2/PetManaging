package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akbirov.petproject.dto.AuthResponseDto;
import ru.akbirov.petproject.dto.LoginDto;
import ru.akbirov.petproject.dto.RegisterDto;
import ru.akbirov.petproject.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        logger.info("Registration attempt for username: {}", registerDto.getUsername());
        AuthResponseDto response = authService.register(registerDto);
        logger.info("User registered successfully: username={}, email={}", 
                response.getUsername(), response.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        logger.info("Login attempt for username: {}", loginDto.getUsername());
        AuthResponseDto response = authService.login(loginDto);
        logger.info("User logged in successfully: username={}, email={}", 
                response.getUsername(), response.getEmail());
        return ResponseEntity.ok(response);
    }
}

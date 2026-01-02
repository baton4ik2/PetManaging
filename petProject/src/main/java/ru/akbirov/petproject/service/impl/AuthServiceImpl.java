package ru.akbirov.petproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.AuthResponseDto;
import ru.akbirov.petproject.dto.LoginDto;
import ru.akbirov.petproject.dto.RegisterDto;
import ru.akbirov.petproject.entity.Role;
import ru.akbirov.petproject.entity.User;
import ru.akbirov.petproject.exception.EmailAlreadyExistsException;
import ru.akbirov.petproject.repository.UserRepository;
import ru.akbirov.petproject.service.AuthService;
import ru.akbirov.petproject.util.RoleUtils;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public AuthResponseDto register(RegisterDto registerDto) {
        logger.info("Registration attempt for username: {}", registerDto.getUsername());
        
        // Проверяем, не существует ли уже пользователь с таким username
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            logger.warn("Username already exists: {}", registerDto.getUsername());
            throw new ru.akbirov.petproject.exception.UsernameAlreadyExistsException(registerDto.getUsername());
        }
        
        // Проверяем, не существует ли уже пользователь с таким email
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            logger.warn("Email already exists: {}", registerDto.getEmail());
            throw new EmailAlreadyExistsException(registerDto.getEmail());
        }
        
        // Создаем нового пользователя
        User user = User.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .enabled(true)
                .build();
        
        user.getRoles().add(Role.USER);
        
        User savedUser = userRepository.save(user);
        
        logger.info("User registered successfully: username={}, email={}, id={}", 
                savedUser.getUsername(), savedUser.getEmail(), savedUser.getId());
        
        // Генерируем токен (пока dummy, потом можно добавить JWT)
        String token = "dummy-token-" + System.currentTimeMillis();
        
        return AuthResponseDto.builder()
                .token(token)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(savedUser.getRoles().stream()
                        .map(role -> RoleUtils.removeRolePrefix("ROLE_" + role.name()))
                        .collect(Collectors.toSet()))
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginDto loginDto) {
        logger.info("Login attempt for username: {}", loginDto.getUsername());
        
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", loginDto.getUsername());
                    return new org.springframework.security.authentication.BadCredentialsException("Invalid username or password");
                });
        
        // Проверяем пароль
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            logger.warn("Invalid password for username: {}", loginDto.getUsername());
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid username or password");
        }
        
        // Проверяем, активен ли пользователь
        if (!user.getEnabled()) {
            logger.warn("User account is disabled: {}", loginDto.getUsername());
            throw new org.springframework.security.authentication.BadCredentialsException("User account is disabled");
        }
        
        logger.info("User logged in successfully: username={}, email={}", user.getUsername(), user.getEmail());
        
        // Генерируем токен (пока dummy, потом можно добавить JWT)
        String token = "dummy-token-" + System.currentTimeMillis();
        
        return AuthResponseDto.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> RoleUtils.removeRolePrefix("ROLE_" + role.name()))
                        .collect(Collectors.toSet()))
                .build();
    }
}


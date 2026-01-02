package ru.akbirov.petproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.UpdateProfileDto;
import ru.akbirov.petproject.dto.UserProfileDto;
import ru.akbirov.petproject.entity.User;
import ru.akbirov.petproject.exception.UserNotFoundException;
import ru.akbirov.petproject.repository.UserRepository;
import ru.akbirov.petproject.service.UserService;
import ru.akbirov.petproject.util.RoleUtils;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile(String username) {
        logger.debug("Getting user profile for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
        
        UserProfileDto dto = UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> RoleUtils.removeRolePrefix("ROLE_" + role.name()))
                        .collect(Collectors.toSet()))
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        
        logger.debug("User profile retrieved: username={}, email={}", user.getUsername(), user.getEmail());
        return dto;
    }
    
    @Override
    @Transactional
    public UserProfileDto updateUserProfile(String username, UpdateProfileDto updateDto) {
        logger.info("Updating user profile for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
        
        // Проверяем, не занят ли email другим пользователем
        if (!user.getEmail().equals(updateDto.getEmail()) 
                && userRepository.existsByEmail(updateDto.getEmail())) {
            logger.warn("Email already exists: {}", updateDto.getEmail());
            throw new ru.akbirov.petproject.exception.EmailAlreadyExistsException(updateDto.getEmail());
        }
        
        user.setEmail(updateDto.getEmail());
        User updatedUser = userRepository.save(user);
        
        UserProfileDto dto = UserProfileDto.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .roles(updatedUser.getRoles().stream()
                        .map(role -> RoleUtils.removeRolePrefix("ROLE_" + role.name()))
                        .collect(Collectors.toSet()))
                .enabled(updatedUser.getEnabled())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
        
        logger.info("User profile updated: username={}, email={}", updatedUser.getUsername(), updatedUser.getEmail());
        return dto;
    }
    
    @Override
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        logger.info("Changing password for username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
        
        // Проверяем текущий пароль
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Invalid current password for username: {}", username);
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid current password");
        }
        
        // Устанавливаем новый пароль
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        logger.info("Password changed successfully for username: {}", username);
    }
}


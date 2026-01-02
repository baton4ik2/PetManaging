package ru.akbirov.petproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.UpdateProfileDto;
import ru.akbirov.petproject.dto.UserProfileDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.User;
import ru.akbirov.petproject.exception.UserNotFoundException;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.UserRepository;
import ru.akbirov.petproject.service.UserService;
import ru.akbirov.petproject.util.RoleUtils;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
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
        
        // Получаем Owner для этого пользователя
        Owner owner = ownerRepository.findByUserId(user.getId()).orElse(null);
        
        UserProfileDto.UserProfileDtoBuilder dtoBuilder = UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(role -> RoleUtils.removeRolePrefix("ROLE_" + role.name()))
                        .collect(Collectors.toSet()))
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt());
        
        // Добавляем данные из Owner, если они есть
        if (owner != null) {
            dtoBuilder.firstName(owner.getFirstName())
                    .lastName(owner.getLastName())
                    .phone(owner.getPhone());
        }
        
        UserProfileDto dto = dtoBuilder.build();
        
        logger.debug("User profile retrieved: username={}, email={}, firstName={}, lastName={}, phone={}", 
                user.getUsername(), user.getEmail(), 
                owner != null ? owner.getFirstName() : null,
                owner != null ? owner.getLastName() : null,
                owner != null ? owner.getPhone() : null);
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
        
        // Обновляем email пользователя
        user.setEmail(updateDto.getEmail());
        User updatedUser = userRepository.save(user);
        
        // Получаем или создаем Owner для этого пользователя
        Owner owner = ownerRepository.findByUserId(updatedUser.getId())
                .orElse(Owner.builder()
                        .user(updatedUser)
                        .email(updatedUser.getEmail())
                        .address("")
                        .build());
        
        // Проверяем, не занят ли телефон другим владельцем
        if ((owner.getPhone() == null || !owner.getPhone().equals(updateDto.getPhone())) 
                && ownerRepository.existsByPhone(updateDto.getPhone())) {
            logger.warn("Phone already exists: {}", updateDto.getPhone());
            throw new ru.akbirov.petproject.exception.PhoneAlreadyExistsException(updateDto.getPhone());
        }
        
        // Обновляем данные Owner
        owner.setFirstName(updateDto.getFirstName());
        owner.setLastName(updateDto.getLastName());
        owner.setPhone(updateDto.getPhone());
        owner.setEmail(updateDto.getEmail());
        Owner updatedOwner = ownerRepository.save(owner);
        
        UserProfileDto dto = UserProfileDto.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .firstName(updatedOwner.getFirstName())
                .lastName(updatedOwner.getLastName())
                .phone(updatedOwner.getPhone())
                .roles(updatedUser.getRoles().stream()
                        .map(role -> RoleUtils.removeRolePrefix("ROLE_" + role.name()))
                        .collect(Collectors.toSet()))
                .enabled(updatedUser.getEnabled())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
        
        logger.info("User profile updated: username={}, email={}, firstName={}, lastName={}, phone={}", 
                updatedUser.getUsername(), updatedUser.getEmail(), 
                updatedOwner.getFirstName(), updatedOwner.getLastName(), updatedOwner.getPhone());
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


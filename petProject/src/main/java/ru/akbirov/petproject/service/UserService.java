package ru.akbirov.petproject.service;

import ru.akbirov.petproject.dto.UserProfileDto;
import ru.akbirov.petproject.dto.UpdateProfileDto;

public interface UserService {
    
    UserProfileDto getCurrentUserProfile(String username);
    
    UserProfileDto updateUserProfile(String username, UpdateProfileDto updateDto);
    
    void changePassword(String username, String currentPassword, String newPassword);
}


package ru.akbirov.petproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.akbirov.petproject.dto.ChangePasswordDto;
import ru.akbirov.petproject.dto.UpdateProfileDto;
import ru.akbirov.petproject.dto.UserProfileDto;
import ru.akbirov.petproject.service.UserService;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Given
        UserProfileDto profileDto = UserProfileDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.getCurrentUserProfile(anyString())).thenReturn(profileDto);

        // When & Then
        mockMvc.perform(get("/api/users/me")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetCurrentUser_UsernameMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCurrentUser_Success() throws Exception {
        // Given
        UpdateProfileDto updateDto = UpdateProfileDto.builder()
                .email("newemail@example.com")
                .build();

        UserProfileDto profileDto = UserProfileDto.builder()
                .id(1L)
                .username("testuser")
                .email("newemail@example.com")
                .roles(Set.of("USER"))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.updateUserProfile(anyString(), any(UpdateProfileDto.class))).thenReturn(profileDto);

        // When & Then
        mockMvc.perform(put("/api/users/me")
                        .param("username", "testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newemail@example.com"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // Given
        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        doNothing().when(userService).changePassword(anyString(), anyString(), anyString());

        // When & Then
        mockMvc.perform(put("/api/users/me/password")
                        .param("username", "testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }
}


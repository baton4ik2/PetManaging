package ru.akbirov.petproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.akbirov.petproject.dto.AuthResponseDto;
import ru.akbirov.petproject.dto.LoginDto;
import ru.akbirov.petproject.dto.RegisterDto;
import ru.akbirov.petproject.service.AuthService;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testRegister_Success() throws Exception {
        // Given
        RegisterDto registerDto = RegisterDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponseDto responseDto = AuthResponseDto.builder()
                .token("test-token")
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();

        when(authService.register(any(RegisterDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        LoginDto loginDto = LoginDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        AuthResponseDto responseDto = AuthResponseDto.builder()
                .token("test-token")
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();

        when(authService.login(any(LoginDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}


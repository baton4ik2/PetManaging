package ru.akbirov.petproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.akbirov.petproject.dto.AuthResponseDto;
import ru.akbirov.petproject.dto.LoginDto;
import ru.akbirov.petproject.dto.RegisterDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.Role;
import ru.akbirov.petproject.entity.User;
import ru.akbirov.petproject.exception.EmailAlreadyExistsException;
import ru.akbirov.petproject.exception.UsernameAlreadyExistsException;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.UserRepository;
import ru.akbirov.petproject.service.impl.AuthServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterDto registerDto;
    private LoginDto loginDto;
    private User user;

    @BeforeEach
    void setUp() {
        registerDto = RegisterDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .build();

        loginDto = LoginDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .enabled(true)
                .build();
        user.getRoles().add(Role.USER);
    }

    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(ownerRepository.save(any(Owner.class))).thenAnswer(invocation -> {
            Owner owner = invocation.getArgument(0);
            owner.setId(1L);
            return owner;
        });

        // When
        AuthResponseDto response = authService.register(registerDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("USER"));
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(ownerRepository, times(1)).save(any(Owner.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        assertThrows(UsernameAlreadyExistsException.class, () -> {
            authService.register(registerDto);
        });
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerDto);
        });
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        AuthResponseDto response = authService.login(loginDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("USER"));
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginDto);
        });
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginDto);
        });
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    void testLogin_UserDisabled() {
        // Given
        user.setEnabled(false);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginDto);
        });
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }
}


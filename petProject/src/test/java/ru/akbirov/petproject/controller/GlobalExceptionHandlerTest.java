package ru.akbirov.petproject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.akbirov.petproject.dto.ErrorResponseDto;
import ru.akbirov.petproject.exception.EmailAlreadyExistsException;
import ru.akbirov.petproject.exception.OwnerNotFoundException;
import ru.akbirov.petproject.exception.PetNotFoundException;
import ru.akbirov.petproject.exception.UserNotFoundException;
import ru.akbirov.petproject.exception.UsernameAlreadyExistsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void testHandleOwnerNotFoundException() {
        // Given
        OwnerNotFoundException ex = new OwnerNotFoundException(1L);

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleOwnerNotFoundException(ex, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Owner not found with id: 1", response.getBody().getMessage());
        assertEquals("Owner Not Found", response.getBody().getError());
    }

    @Test
    void testHandlePetNotFoundException() {
        // Given
        PetNotFoundException ex = new PetNotFoundException(1L);

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handlePetNotFoundException(ex, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Pet not found with id: 1", response.getBody().getMessage());
        assertEquals("Pet Not Found", response.getBody().getError());
    }

    @Test
    void testHandleEmailAlreadyExistsException() {
        // Given
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("test@example.com");

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleEmailAlreadyExistsException(ex, request);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email already exists: test@example.com", response.getBody().getMessage());
        assertEquals("Email Already Exists", response.getBody().getError());
    }

    @Test
    void testHandleUsernameAlreadyExistsException() {
        // Given
        UsernameAlreadyExistsException ex = new UsernameAlreadyExistsException("testuser");

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleUsernameAlreadyExistsException(ex, request);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Username already exists: testuser", response.getBody().getMessage());
        assertEquals("Username Already Exists", response.getBody().getError());
    }

    @Test
    void testHandleUserNotFoundException() {
        // Given
        UserNotFoundException ex = new UserNotFoundException("User not found");

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleUserNotFoundException(ex, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals("User Not Found", response.getBody().getError());
    }

    @Test
    void testHandleBadCredentialsException() {
        // Given
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleBadCredentialsException(ex, request);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid username or password", response.getBody().getMessage());
        assertEquals("Bad Credentials", response.getBody().getError());
    }

    @Test
    void testHandleDataIntegrityViolationException_Username() {
        // Given
        DataIntegrityViolationException ex = new DataIntegrityViolationException("users_username_key");

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleDataIntegrityViolationException(ex, request);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Username already exists", response.getBody().getMessage());
    }

    @Test
    void testHandleDataIntegrityViolationException_Email() {
        // Given
        DataIntegrityViolationException ex = new DataIntegrityViolationException("users_email_key");

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleDataIntegrityViolationException(ex, request);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email already exists", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationException() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.List.of(fieldError));

        // When
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationException(ex, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().get("message"));
        assertNotNull(response.getBody().get("errors"));
    }

    @Test
    void testHandleGenericException() {
        // Given
        RuntimeException ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleGenericException(ex, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getMessage());
    }
}


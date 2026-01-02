package ru.akbirov.petproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.akbirov.petproject.dto.OwnerRequestDto;
import ru.akbirov.petproject.dto.OwnerResponseDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.exception.AccessDeniedException;
import ru.akbirov.petproject.service.OwnerService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OwnerService ownerService;

    private Authentication createAdminAuthentication() {
        Authentication auth = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_ADMIN");
        java.util.List<GrantedAuthority> authoritiesList = Collections.singletonList(authority);
        when(auth.getAuthorities()).thenAnswer(invocation -> authoritiesList);
        when(auth.getName()).thenReturn("admin");
        when(auth.isAuthenticated()).thenReturn(true);
        return auth;
    }

    private Authentication createUserAuthentication() {
        Authentication auth = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_USER");
        java.util.List<GrantedAuthority> authoritiesList = Collections.singletonList(authority);
        when(auth.getAuthorities()).thenAnswer(invocation -> authoritiesList);
        when(auth.getName()).thenReturn("user");
        when(auth.isAuthenticated()).thenReturn(true);
        return auth;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateOwner_Success() throws Exception {
        // Given
        OwnerRequestDto requestDto = OwnerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+7 999 999 99 99")
                .address("123 Main St")
                .build();

        OwnerResponseDto responseDto = OwnerResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+7 999 999 99 99")
                .address("123 Main St")
                .build();

        when(ownerService.createOwner(any(OwnerRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/owners")
                        .with(authentication(createAdminAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateOwner_AccessDenied() throws Exception {
        // Given
        OwnerRequestDto requestDto = OwnerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+7 999 999 99 99")
                .address("123 Main St")
                .build();

        // When & Then
        mockMvc.perform(post("/api/owners")
                        .with(authentication(createUserAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetOwnerById_Success() throws Exception {
        // Given
        OwnerResponseDto responseDto = OwnerResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+7 999 999 99 99")
                .address("123 Main St")
                .build();

        when(ownerService.getOwnerById(1L)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testGetAllOwners_Success() throws Exception {
        // Given
        OwnerResponseDto owner1 = OwnerResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        OwnerResponseDto owner2 = OwnerResponseDto.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .build();

        when(ownerService.getAllOwners()).thenReturn(List.of(owner1, owner2));

        // When & Then
        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void testGetAllOwners_WithSearch() throws Exception {
        // Given
        OwnerResponseDto owner = OwnerResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        when(ownerService.search("John")).thenReturn(List.of(owner));

        // When & Then
        mockMvc.perform(get("/api/owners")
                        .param("search", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateOwner_Success() throws Exception {
        // Given
        OwnerRequestDto requestDto = OwnerRequestDto.builder()
                .firstName("John")
                .lastName("Doe Updated")
                .email("john@example.com")
                .phone("+7 999 999 99 99")
                .address("123 Main St")
                .build();

        OwnerResponseDto responseDto = OwnerResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe Updated")
                .email("john@example.com")
                .phone("+7 999 999 99 99")
                .address("123 Main St")
                .build();

        when(ownerService.updateOwner(eq(1L), any(OwnerRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/owners/1")
                        .with(authentication(createAdminAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Doe Updated"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateOwner_AccessDenied() throws Exception {
        // Given
        OwnerRequestDto requestDto = OwnerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+7 999 999 99 99")
                .address("123 Main St")
                .build();

        // When & Then
        mockMvc.perform(put("/api/owners/1")
                        .with(authentication(createUserAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteOwner_Success() throws Exception {
        // Given
        doNothing().when(ownerService).deleteOwner(1L);

        // When & Then
        mockMvc.perform(delete("/api/owners/1")
                        .with(authentication(createAdminAuthentication())))
                .andExpect(status().isNoContent());

        verify(ownerService, times(1)).deleteOwner(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteOwner_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/owners/1")
                        .with(authentication(createUserAuthentication())))
                .andExpect(status().isForbidden());

        verify(ownerService, never()).deleteOwner(anyLong());
    }

    @Test
    void testGetOwnerPets_Success() throws Exception {
        // Given
        PetResponseDto pet1 = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .type(ru.akbirov.petproject.entity.PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        PetResponseDto pet2 = PetResponseDto.builder()
                .id(2L)
                .name("Luna")
                .type(ru.akbirov.petproject.entity.PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.of(2022, 3, 20))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        when(ownerService.getOwnerPets(1L)).thenReturn(List.of(pet1, pet2));

        // When & Then
        mockMvc.perform(get("/api/owners/1/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Max"))
                .andExpect(jsonPath("$[1].name").value("Luna"));
    }
}


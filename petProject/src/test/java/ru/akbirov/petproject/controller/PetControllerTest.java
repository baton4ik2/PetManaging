package ru.akbirov.petproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.akbirov.petproject.dto.PetRequestDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.service.PetService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    private Authentication createAdminAuthentication() {
        Authentication auth = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_ADMIN");
        @SuppressWarnings("unchecked")
        java.util.Collection<GrantedAuthority> authorities = (java.util.Collection<GrantedAuthority>) Collections.singletonList(authority);
        when(auth.getAuthorities()).thenReturn(authorities);
        when(auth.getName()).thenReturn("admin");
        when(auth.isAuthenticated()).thenReturn(true);
        return auth;
    }

    private Authentication createUserAuthentication(String username) {
        Authentication auth = mock(Authentication.class);
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_USER");
        @SuppressWarnings("unchecked")
        java.util.Collection<GrantedAuthority> authorities = (java.util.Collection<GrantedAuthority>) Collections.singletonList(authority);
        when(auth.getAuthorities()).thenReturn(authorities);
        when(auth.getName()).thenReturn(username);
        when(auth.isAuthenticated()).thenReturn(true);
        return auth;
    }

    @Test
    void testCreatePet_Success() throws Exception {
        // Given
        PetRequestDto requestDto = PetRequestDto.builder()
                .name("Max")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .color("Golden")
                .description("Friendly dog")
                .ownerId(1L)
                .build();

        PetResponseDto responseDto = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .color("Golden")
                .description("Friendly dog")
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        when(petService.createPet(any(PetRequestDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.type").value("DOG"));
    }

    @Test
    void testGetPetById_Success() throws Exception {
        // Given
        PetResponseDto responseDto = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        when(petService.getPetById(1L)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Max"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetMyPets_Success() throws Exception {
        // Given
        PetResponseDto pet = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("Test User")
                .build();

        when(petService.getMyPets("testuser")).thenReturn(List.of(pet));

        // When & Then
        mockMvc.perform(get("/api/pets/my")
                        .with(authentication(createUserAuthentication("testuser"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Max"));
    }

    @Test
    void testGetAllPets_Success() throws Exception {
        // Given
        PetResponseDto pet1 = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        PetResponseDto pet2 = PetResponseDto.builder()
                .id(2L)
                .name("Luna")
                .type(PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.of(2022, 3, 20))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        when(petService.getAllPets()).thenReturn(List.of(pet1, pet2));

        // When & Then
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllPets_WithTypeFilter() throws Exception {
        // Given
        PetResponseDto pet = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        when(petService.getPetsByType(PetType.DOG)).thenReturn(List.of(pet));

        // When & Then
        mockMvc.perform(get("/api/pets")
                        .param("type", "DOG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("DOG"));
    }

    @Test
    void testGetAllPets_WithSearch() throws Exception {
        // Given
        PetResponseDto pet = PetResponseDto.builder()
                .id(1L)
                .name("Max")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        when(petService.search("Max")).thenReturn(List.of(pet));

        // When & Then
        mockMvc.perform(get("/api/pets")
                        .param("search", "Max"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Max"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePet_AsAdmin_Success() throws Exception {
        // Given
        PetRequestDto requestDto = PetRequestDto.builder()
                .name("Max Updated")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .build();

        PetResponseDto responseDto = PetResponseDto.builder()
                .id(1L)
                .name("Max Updated")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("John Doe")
                .build();

        when(petService.updatePet(eq(1L), any(PetRequestDto.class))).thenReturn(responseDto);
        when(petService.isPetOwner(1L, "admin")).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/pets/1")
                        .with(authentication(createAdminAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max Updated"));
    }

    @Test
    @WithMockUser(username = "owner")
    void testUpdatePet_AsOwner_Success() throws Exception {
        // Given
        PetRequestDto requestDto = PetRequestDto.builder()
                .name("Max Updated")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .build();

        PetResponseDto responseDto = PetResponseDto.builder()
                .id(1L)
                .name("Max Updated")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .ownerName("Owner")
                .build();

        when(petService.updatePet(eq(1L), any(PetRequestDto.class))).thenReturn(responseDto);
        when(petService.isPetOwner(1L, "owner")).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/pets/1")
                        .with(authentication(createUserAuthentication("owner")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max Updated"));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void testUpdatePet_AsOtherUser_AccessDenied() throws Exception {
        // Given
        PetRequestDto requestDto = PetRequestDto.builder()
                .name("Max Updated")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.of(2021, 6, 15))
                .ownerId(1L)
                .build();

        when(petService.isPetOwner(1L, "otheruser")).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/api/pets/1")
                        .with(authentication(createUserAuthentication("otheruser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletePet_AsAdmin_Success() throws Exception {
        // Given
        doNothing().when(petService).deletePet(1L);
        when(petService.isPetOwner(1L, "admin")).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/pets/1")
                        .with(authentication(createAdminAuthentication())))
                .andExpect(status().isNoContent());

        verify(petService, times(1)).deletePet(1L);
    }

    @Test
    @WithMockUser(username = "owner")
    void testDeletePet_AsOwner_Success() throws Exception {
        // Given
        doNothing().when(petService).deletePet(1L);
        when(petService.isPetOwner(1L, "owner")).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/pets/1")
                        .with(authentication(createUserAuthentication("owner"))))
                .andExpect(status().isNoContent());

        verify(petService, times(1)).deletePet(1L);
    }
}


package ru.akbirov.petproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akbirov.petproject.dto.PetRequestDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.Pet;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.exception.OwnerNotFoundException;
import ru.akbirov.petproject.exception.PetNotFoundException;
import ru.akbirov.petproject.mapper.PetMapper;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.PetRepository;
import ru.akbirov.petproject.service.impl.PetServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private PetServiceImpl petService;

    private PetRequestDto petRequestDto;
    private Pet pet;
    private PetResponseDto petResponseDto;
    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = Owner.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        petRequestDto = PetRequestDto.builder()
                .name("Buddy")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .color("Golden")
                .description("Friendly dog")
                .ownerId(1L)
                .build();

        pet = Pet.builder()
                .id(1L)
                .name("Buddy")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .color("Golden")
                .description("Friendly dog")
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        petResponseDto = PetResponseDto.builder()
                .id(1L)
                .name("Buddy")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .color("Golden")
                .description("Friendly dog")
                .ownerId(1L)
                .build();
    }

    @Test
    void testCreatePet_Success() {
        // Given
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(petMapper.toEntity(any(PetRequestDto.class))).thenReturn(pet);
        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        PetResponseDto result = petService.createPet(petRequestDto);

        // Then
        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        assertEquals(PetType.DOG, result.getType());
        verify(ownerRepository, times(1)).findById(1L);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void testCreatePet_OwnerNotFound() {
        // Given
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OwnerNotFoundException.class, () -> {
            petService.createPet(petRequestDto);
        });
        verify(ownerRepository, times(1)).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void testGetPetById_Success() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        PetResponseDto result = petService.getPetById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Buddy", result.getName());
        verify(petRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPetById_NotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PetNotFoundException.class, () -> {
            petService.getPetById(1L);
        });
        verify(petRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllPets_Success() {
        // Given
        List<Pet> pets = List.of(pet);
        when(petRepository.findAll()).thenReturn(pets);
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        List<PetResponseDto> result = petService.getAllPets();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    void testGetPetsByType_Success() {
        // Given
        List<Pet> pets = List.of(pet);
        when(petRepository.findByType(PetType.DOG)).thenReturn(pets);
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        List<PetResponseDto> result = petService.getPetsByType(PetType.DOG);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(petRepository, times(1)).findByType(PetType.DOG);
    }

    @Test
    void testGetPetsByOwnerId_Success() {
        // Given
        List<Pet> pets = List.of(pet);
        when(petRepository.findByOwnerId(1L)).thenReturn(pets);
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        List<PetResponseDto> result = petService.getPetsByOwnerId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(petRepository, times(1)).findByOwnerId(1L);
    }

    @Test
    void testUpdatePet_Success() {
        // Given
        PetRequestDto updateDto = PetRequestDto.builder()
                .name("Max")
                .type(PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.now().minusYears(1))
                .color("White")
                .description("Calm cat")
                .ownerId(1L)
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        PetResponseDto result = petService.updatePet(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(petRepository, times(1)).findById(1L);
        verify(ownerRepository, times(1)).findById(1L);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void testUpdatePet_PetNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PetNotFoundException.class, () -> {
            petService.updatePet(1L, petRequestDto);
        });
        verify(petRepository, times(1)).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void testUpdatePet_OwnerNotFound() {
        // Given
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OwnerNotFoundException.class, () -> {
            petService.updatePet(1L, petRequestDto);
        });
        verify(petRepository, times(1)).findById(1L);
        verify(ownerRepository, times(1)).findById(1L);
        verify(petRepository, never()).save(any(Pet.class));
    }

    @Test
    void testDeletePet_Success() {
        // Given
        when(petRepository.existsById(1L)).thenReturn(true);
        doNothing().when(petRepository).deleteById(1L);

        // When
        petService.deletePet(1L);

        // Then
        verify(petRepository, times(1)).existsById(1L);
        verify(petRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePet_NotFound() {
        // Given
        when(petRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(PetNotFoundException.class, () -> {
            petService.deletePet(1L);
        });
        verify(petRepository, times(1)).existsById(1L);
        verify(petRepository, never()).deleteById(anyLong());
    }

    @Test
    void testSearch_Success() {
        // Given
        List<Pet> pets = List.of(pet);
        when(petRepository.search("Buddy")).thenReturn(pets);
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        List<PetResponseDto> result = petService.search("Buddy");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(petRepository, times(1)).search("Buddy");
    }
}


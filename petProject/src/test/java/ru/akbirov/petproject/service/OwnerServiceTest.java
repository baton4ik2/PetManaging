package ru.akbirov.petproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akbirov.petproject.dto.OwnerRequestDto;
import ru.akbirov.petproject.dto.OwnerResponseDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.Pet;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.exception.EmailAlreadyExistsException;
import ru.akbirov.petproject.exception.OwnerNotFoundException;
import ru.akbirov.petproject.mapper.OwnerMapper;
import ru.akbirov.petproject.mapper.PetMapper;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.service.impl.OwnerServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerMapper ownerMapper;

    @Mock
    private PetMapper petMapper;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private OwnerRequestDto ownerRequestDto;
    private Owner owner;
    private OwnerResponseDto ownerResponseDto;
    private Pet pet;

    @BeforeEach
    void setUp() {
        ownerRequestDto = OwnerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();

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

        ownerResponseDto = OwnerResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();

        pet = Pet.builder()
                .id(1L)
                .name("Buddy")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .owner(owner)
                .build();

        owner.setPets(new ArrayList<>());
    }

    @Test
    void testCreateOwner_Success() {
        // Given
        when(ownerRepository.existsByEmail(anyString())).thenReturn(false);
        when(ownerMapper.toEntity(any(OwnerRequestDto.class))).thenReturn(owner);
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
        when(ownerMapper.toResponseDto(any(Owner.class))).thenReturn(ownerResponseDto);

        // When
        OwnerResponseDto result = ownerService.createOwner(ownerRequestDto);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(ownerRepository, times(1)).existsByEmail("john@example.com");
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    void testCreateOwner_EmailAlreadyExists() {
        // Given
        when(ownerRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            ownerService.createOwner(ownerRequestDto);
        });
        verify(ownerRepository, times(1)).existsByEmail("john@example.com");
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void testGetOwnerById_Success() {
        // Given
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ownerMapper.toResponseDto(any(Owner.class))).thenReturn(ownerResponseDto);

        // When
        OwnerResponseDto result = ownerService.getOwnerById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOwnerById_NotFound() {
        // Given
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OwnerNotFoundException.class, () -> {
            ownerService.getOwnerById(1L);
        });
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllOwners_Success() {
        // Given
        List<Owner> owners = List.of(owner);
        when(ownerRepository.findAll()).thenReturn(owners);
        when(ownerMapper.toResponseDto(any(Owner.class))).thenReturn(ownerResponseDto);

        // When
        List<OwnerResponseDto> result = ownerService.getAllOwners();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ownerRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOwner_Success() {
        // Given
        OwnerRequestDto updateDto = OwnerRequestDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phone("9876543210")
                .address("456 Oak Ave")
                .build();

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ownerRepository.existsByEmail(anyString())).thenReturn(false);
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
        when(ownerMapper.toResponseDto(any(Owner.class))).thenReturn(ownerResponseDto);

        // When
        OwnerResponseDto result = ownerService.updateOwner(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, times(1)).existsByEmail("jane@example.com");
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    void testUpdateOwner_NotFound() {
        // Given
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OwnerNotFoundException.class, () -> {
            ownerService.updateOwner(1L, ownerRequestDto);
        });
        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void testUpdateOwner_EmailAlreadyExists() {
        // Given
        OwnerRequestDto updateDto = OwnerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("existing@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ownerRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            ownerService.updateOwner(1L, updateDto);
        });
        verify(ownerRepository, times(1)).findById(1L);
        verify(ownerRepository, times(1)).existsByEmail("existing@example.com");
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void testDeleteOwner_Success() {
        // Given
        when(ownerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ownerRepository).deleteById(1L);

        // When
        ownerService.deleteOwner(1L);

        // Then
        verify(ownerRepository, times(1)).existsById(1L);
        verify(ownerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteOwner_NotFound() {
        // Given
        when(ownerRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(OwnerNotFoundException.class, () -> {
            ownerService.deleteOwner(1L);
        });
        verify(ownerRepository, times(1)).existsById(1L);
        verify(ownerRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetOwnerPets_Success() {
        // Given
        owner.getPets().add(pet);
        PetResponseDto petResponseDto = PetResponseDto.builder()
                .id(1L)
                .name("Buddy")
                .type(PetType.DOG)
                .build();

        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(petMapper.toResponseDto(any(Pet.class))).thenReturn(petResponseDto);

        // When
        List<PetResponseDto> result = ownerService.getOwnerPets(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOwnerPets_OwnerNotFound() {
        // Given
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OwnerNotFoundException.class, () -> {
            ownerService.getOwnerPets(1L);
        });
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    void testSearch_Success() {
        // Given
        List<Owner> owners = List.of(owner);
        when(ownerRepository.search("John")).thenReturn(owners);
        when(ownerMapper.toResponseDto(any(Owner.class))).thenReturn(ownerResponseDto);

        // When
        List<OwnerResponseDto> result = ownerService.search("John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ownerRepository, times(1)).search("John");
    }
}


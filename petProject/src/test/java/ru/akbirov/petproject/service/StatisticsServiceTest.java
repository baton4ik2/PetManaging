package ru.akbirov.petproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akbirov.petproject.dto.StatisticsDto;
import ru.akbirov.petproject.entity.Pet;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.PetRepository;
import ru.akbirov.petproject.service.impl.StatisticsServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @BeforeEach
    void setUp() {
        // Setup common mocks
    }

    @Test
    void testGetStatistics_Success() {
        // Given
        when(ownerRepository.count()).thenReturn(10L);
        when(petRepository.count()).thenReturn(25L);
        when(petRepository.findByType(PetType.DOG)).thenReturn(List.of(
                Pet.builder().id(1L).type(PetType.DOG).build(),
                Pet.builder().id(2L).type(PetType.DOG).build()
        ));
        when(petRepository.findByType(PetType.CAT)).thenReturn(List.of(
                Pet.builder().id(3L).type(PetType.CAT).build()
        ));
        when(petRepository.findByType(PetType.BIRD)).thenReturn(List.of());
        when(petRepository.findByType(PetType.OTHER)).thenReturn(List.of());

        // When
        StatisticsDto result = statisticsService.getStatistics();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalOwners());
        assertEquals(25L, result.getTotalPets());
        assertEquals(2L, result.getPetsByType().get("DOG"));
        assertEquals(1L, result.getPetsByType().get("CAT"));
        assertEquals(0L, result.getPetsByType().get("BIRD"));
        assertEquals(0L, result.getPetsByType().get("OTHER"));
        assertEquals(2L, result.getAveragePetsPerOwner()); // 25 / 10 = 2
        verify(ownerRepository, times(1)).count();
        verify(petRepository, times(1)).count();
    }

    @Test
    void testGetStatistics_NoOwners() {
        // Given
        when(ownerRepository.count()).thenReturn(0L);
        when(petRepository.count()).thenReturn(0L);
        when(petRepository.findByType(any(PetType.class))).thenReturn(List.of());

        // When
        StatisticsDto result = statisticsService.getStatistics();

        // Then
        assertNotNull(result);
        assertEquals(0L, result.getTotalOwners());
        assertEquals(0L, result.getTotalPets());
        assertEquals(0L, result.getAveragePetsPerOwner());
        verify(ownerRepository, times(1)).count();
        verify(petRepository, times(1)).count();
    }

    @Test
    void testGetStatistics_AllPetTypes() {
        // Given
        when(ownerRepository.count()).thenReturn(5L);
        when(petRepository.count()).thenReturn(10L);
        when(petRepository.findByType(PetType.DOG)).thenReturn(List.of(
                Pet.builder().id(1L).type(PetType.DOG).build(),
                Pet.builder().id(2L).type(PetType.DOG).build(),
                Pet.builder().id(3L).type(PetType.DOG).build()
        ));
        when(petRepository.findByType(PetType.CAT)).thenReturn(List.of(
                Pet.builder().id(4L).type(PetType.CAT).build(),
                Pet.builder().id(5L).type(PetType.CAT).build()
        ));
        when(petRepository.findByType(PetType.BIRD)).thenReturn(List.of(
                Pet.builder().id(6L).type(PetType.BIRD).build()
        ));
        when(petRepository.findByType(PetType.OTHER)).thenReturn(List.of(
                Pet.builder().id(7L).type(PetType.OTHER).build(),
                Pet.builder().id(8L).type(PetType.OTHER).build(),
                Pet.builder().id(9L).type(PetType.OTHER).build(),
                Pet.builder().id(10L).type(PetType.OTHER).build()
        ));

        // When
        StatisticsDto result = statisticsService.getStatistics();

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getPetsByType().get("DOG"));
        assertEquals(2L, result.getPetsByType().get("CAT"));
        assertEquals(1L, result.getPetsByType().get("BIRD"));
        assertEquals(4L, result.getPetsByType().get("OTHER"));
    }
}


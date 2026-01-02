package ru.akbirov.petproject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.akbirov.petproject.config.AbstractTestcontainersTest;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.Pet;
import ru.akbirov.petproject.entity.PetType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PetRepositoryTest extends AbstractTestcontainersTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner owner;
    private Pet pet;

    @BeforeEach
    void setUp() {
        owner = Owner.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();
        entityManager.persistAndFlush(owner);

        pet = Pet.builder()
                .name("Buddy")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .color("Golden")
                .description("Friendly dog")
                .owner(owner)
                .build();
    }

    @Test
    void testSavePet() {
        // When
        Pet saved = petRepository.save(pet);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Buddy", saved.getName());
        assertEquals(PetType.DOG, saved.getType());
        assertEquals(owner.getId(), saved.getOwner().getId());
    }

    @Test
    void testFindById() {
        // Given
        entityManager.persistAndFlush(pet);

        // When
        Optional<Pet> found = petRepository.findById(pet.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Buddy", found.get().getName());
    }

    @Test
    void testFindAll() {
        // Given
        entityManager.persistAndFlush(pet);
        Pet pet2 = Pet.builder()
                .name("Max")
                .type(PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.now().minusYears(1))
                .owner(owner)
                .build();
        entityManager.persistAndFlush(pet2);

        // When
        List<Pet> pets = petRepository.findAll();

        // Then
        assertEquals(2, pets.size());
    }

    @Test
    void testFindByType() {
        // Given
        entityManager.persistAndFlush(pet);
        Pet pet2 = Pet.builder()
                .name("Max")
                .type(PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.now().minusYears(1))
                .owner(owner)
                .build();
        entityManager.persistAndFlush(pet2);

        // When
        List<Pet> dogs = petRepository.findByType(PetType.DOG);

        // Then
        assertEquals(1, dogs.size());
        assertEquals("Buddy", dogs.get(0).getName());
    }

    @Test
    void testFindByOwnerId() {
        // Given
        entityManager.persistAndFlush(pet);
        Owner owner2 = Owner.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phone("9876543210")
                .address("456 Oak Ave")
                .build();
        entityManager.persistAndFlush(owner2);
        Pet pet2 = Pet.builder()
                .name("Max")
                .type(PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.now().minusYears(1))
                .owner(owner2)
                .build();
        entityManager.persistAndFlush(pet2);

        // When
        List<Pet> ownerPets = petRepository.findByOwnerId(owner.getId());

        // Then
        assertEquals(1, ownerPets.size());
        assertEquals("Buddy", ownerPets.get(0).getName());
    }

    @Test
    void testDeletePet() {
        // Given
        entityManager.persistAndFlush(pet);
        Long id = pet.getId();

        // When
        petRepository.deleteById(id);
        entityManager.flush();

        // Then
        Optional<Pet> found = petRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testCascadeDelete_WhenOwnerDeleted() {
        // Given
        entityManager.persistAndFlush(pet);
        Long petId = pet.getId();
        Long ownerId = owner.getId();

        // When
        ownerRepository.deleteById(ownerId);
        entityManager.flush();

        // Then
        Optional<Pet> found = petRepository.findById(petId);
        assertFalse(found.isPresent());
    }
}


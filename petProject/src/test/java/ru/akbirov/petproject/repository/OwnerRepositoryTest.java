package ru.akbirov.petproject.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.akbirov.petproject.config.AbstractTestcontainersTest;
import ru.akbirov.petproject.entity.Owner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OwnerRepositoryTest extends AbstractTestcontainersTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = Owner.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();
    }

    @Test
    void testSaveOwner() {
        // When
        Owner saved = ownerRepository.save(owner);

        // Then
        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("john@example.com", saved.getEmail());
    }

    @Test
    void testFindById() {
        // Given
        entityManager.persistAndFlush(owner);

        // When
        Optional<Owner> found = ownerRepository.findById(owner.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    void testFindAll() {
        // Given
        entityManager.persistAndFlush(owner);
        Owner owner2 = Owner.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phone("9876543210")
                .address("456 Oak Ave")
                .build();
        entityManager.persistAndFlush(owner2);

        // When
        List<Owner> owners = ownerRepository.findAll();

        // Then
        assertEquals(2, owners.size());
    }

    @Test
    void testExistsByEmail() {
        // Given
        entityManager.persistAndFlush(owner);

        // When
        boolean exists = ownerRepository.existsByEmail("john@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_NotFound() {
        // When
        boolean exists = ownerRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testUniqueEmailConstraint() {
        // Given
        entityManager.persistAndFlush(owner);
        Owner duplicateOwner = Owner.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("john@example.com")
                .phone("9876543210")
                .address("456 Oak Ave")
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            ownerRepository.saveAndFlush(duplicateOwner);
        });
    }

    @Test
    void testDeleteOwner() {
        // Given
        entityManager.persistAndFlush(owner);
        Long id = owner.getId();

        // When
        ownerRepository.deleteById(id);
        entityManager.flush();

        // Then
        Optional<Owner> found = ownerRepository.findById(id);
        assertFalse(found.isPresent());
    }
}


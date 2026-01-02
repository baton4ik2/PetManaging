package ru.akbirov.petproject.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.config.AbstractTestcontainersTest;
import ru.akbirov.petproject.dto.*;
import ru.akbirov.petproject.entity.*;
import ru.akbirov.petproject.repository.*;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTest extends AbstractTestcontainersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .enabled(true)
                .build();
        testUser.getRoles().add(Role.USER);
        userRepository.save(testUser);

        // Login to get token
        LoginDto loginDto = LoginDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testFullOwnerLifecycle() throws Exception {
        // Create Owner
        OwnerRequestDto ownerRequest = OwnerRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();

        String ownerJson = mockMvc.perform(post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long ownerId = objectMapper.readTree(ownerJson).get("id").asLong();

        // Get Owner
        mockMvc.perform(get("/api/owners/{id}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        // Update Owner
        OwnerRequestDto updateRequest = OwnerRequestDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .phone("9876543210")
                .address("456 Oak Ave")
                .build();

        mockMvc.perform(put("/api/owners/{id}", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));

        // Delete Owner
        mockMvc.perform(delete("/api/owners/{id}", ownerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFullPetLifecycle() throws Exception {
        // Create Owner first
        Owner owner = Owner.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();
        owner = ownerRepository.save(owner);

        // Create Pet
        PetRequestDto petRequest = PetRequestDto.builder()
                .name("Buddy")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .color("Golden")
                .description("Friendly dog")
                .ownerId(owner.getId())
                .build();

        String petJson = mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long petId = objectMapper.readTree(petJson).get("id").asLong();

        // Get Pet
        mockMvc.perform(get("/api/pets/{id}", petId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Buddy"));

        // Update Pet
        PetRequestDto updateRequest = PetRequestDto.builder()
                .name("Max")
                .type(PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.now().minusYears(1))
                .color("White")
                .ownerId(owner.getId())
                .build();

        mockMvc.perform(put("/api/pets/{id}", petId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max"));

        // Delete Pet
        mockMvc.perform(delete("/api/pets/{id}", petId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUserRegistrationAndProfile() throws Exception {
        // Register new user
        RegisterDto registerDto = RegisterDto.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("Jane")
                .lastName("Smith")
                .phone("9876543210")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));

        // Get profile
        mockMvc.perform(get("/api/users/me")
                        .param("username", "newuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));

        // Update profile
        UpdateProfileDto updateDto = UpdateProfileDto.builder()
                .email("updated@example.com")
                .build();

        mockMvc.perform(put("/api/users/me")
                        .param("username", "newuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testStatistics() throws Exception {
        // Create test data
        Owner owner = Owner.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("1234567890")
                .address("123 Main St")
                .build();
        owner = ownerRepository.save(owner);

        Pet pet1 = Pet.builder()
                .name("Buddy")
                .type(PetType.DOG)
                .breed("Golden Retriever")
                .dateOfBirth(LocalDate.now().minusYears(2))
                .owner(owner)
                .build();
        petRepository.save(pet1);

        Pet pet2 = Pet.builder()
                .name("Max")
                .type(PetType.CAT)
                .breed("Persian")
                .dateOfBirth(LocalDate.now().minusYears(1))
                .owner(owner)
                .build();
        petRepository.save(pet2);

        // Get statistics
        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOwners").exists())
                .andExpect(jsonPath("$.totalPets").exists())
                .andExpect(jsonPath("$.petsByType").exists());
    }
}


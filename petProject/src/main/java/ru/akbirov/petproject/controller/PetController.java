package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akbirov.petproject.dto.PetRequestDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.service.PetService;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "API для управления питомцами")
public class PetController {
    
    private static final Logger logger = LoggerFactory.getLogger(PetController.class);
    private final PetService petService;
    
    @PostMapping
    @Operation(summary = "Создать нового питомца")
    public ResponseEntity<PetResponseDto> createPet(@Valid @RequestBody PetRequestDto requestDto) {
        logger.info("Creating new pet: {} (type: {}, ownerId: {})", 
                requestDto.getName(), requestDto.getType(), requestDto.getOwnerId());
        PetResponseDto response = petService.createPet(requestDto);
        logger.info("Pet created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить питомца по ID")
    public ResponseEntity<PetResponseDto> getPetById(@PathVariable Long id) {
        logger.debug("Getting pet by ID: {}", id);
        PetResponseDto response = petService.getPetById(id);
        logger.debug("Pet retrieved: {} (ID: {})", response.getName(), response.getId());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Получить всех питомцев")
    public ResponseEntity<List<PetResponseDto>> getAllPets(
            @RequestParam(required = false) PetType type,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String search) {
        logger.debug("Getting pets with filters: type={}, ownerId={}, search={}", type, ownerId, search);
        List<PetResponseDto> response;
        if (search != null && !search.trim().isEmpty()) {
            logger.info("Searching pets with query: {}", search);
            response = petService.search(search.trim());
        } else if (type != null && ownerId != null) {
            logger.debug("Filtering pets by type {} and ownerId {}", type, ownerId);
            response = petService.getPetsByOwnerId(ownerId).stream()
                    .filter(pet -> pet.getType() == type)
                    .toList();
        } else if (type != null) {
            logger.debug("Filtering pets by type: {}", type);
            response = petService.getPetsByType(type);
        } else if (ownerId != null) {
            logger.debug("Filtering pets by ownerId: {}", ownerId);
            response = petService.getPetsByOwnerId(ownerId);
        } else {
            logger.debug("Getting all pets");
            response = petService.getAllPets();
        }
        logger.debug("Found {} pets", response.size());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Обновить питомца")
    public ResponseEntity<PetResponseDto> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetRequestDto requestDto) {
        logger.info("Updating pet with ID: {}", id);
        PetResponseDto response = petService.updatePet(id, requestDto);
        logger.info("Pet updated successfully: {} (ID: {})", response.getName(), response.getId());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить питомца")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        logger.info("Deleting pet with ID: {}", id);
        petService.deletePet(id);
        logger.info("Pet deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}


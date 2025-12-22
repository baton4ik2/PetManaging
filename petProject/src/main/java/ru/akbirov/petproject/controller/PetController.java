package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    
    private final PetService petService;
    
    @PostMapping
    @Operation(summary = "Создать нового питомца")
    public ResponseEntity<PetResponseDto> createPet(@Valid @RequestBody PetRequestDto requestDto) {
        PetResponseDto response = petService.createPet(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить питомца по ID")
    public ResponseEntity<PetResponseDto> getPetById(@PathVariable Long id) {
        PetResponseDto response = petService.getPetById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Получить всех питомцев")
    public ResponseEntity<List<PetResponseDto>> getAllPets(
            @RequestParam(required = false) PetType type,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String search) {
        List<PetResponseDto> response;
        if (search != null && !search.trim().isEmpty()) {
            response = petService.search(search.trim());
        } else if (type != null && ownerId != null) {
            response = petService.getPetsByOwnerId(ownerId).stream()
                    .filter(pet -> pet.getType() == type)
                    .toList();
        } else if (type != null) {
            response = petService.getPetsByType(type);
        } else if (ownerId != null) {
            response = petService.getPetsByOwnerId(ownerId);
        } else {
            response = petService.getAllPets();
        }
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Обновить питомца")
    public ResponseEntity<PetResponseDto> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetRequestDto requestDto) {
        PetResponseDto response = petService.updatePet(id, requestDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить питомца")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}


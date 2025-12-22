package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akbirov.petproject.dto.OwnerRequestDto;
import ru.akbirov.petproject.dto.OwnerResponseDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.service.OwnerService;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Tag(name = "Owners", description = "API для управления владельцами питомцев")
public class OwnerController {
    
    private final OwnerService ownerService;
    
    @PostMapping
    @Operation(summary = "Создать нового владельца")
    public ResponseEntity<OwnerResponseDto> createOwner(@Valid @RequestBody OwnerRequestDto requestDto) {
        OwnerResponseDto response = ownerService.createOwner(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить владельца по ID")
    public ResponseEntity<OwnerResponseDto> getOwnerById(@PathVariable Long id) {
        OwnerResponseDto response = ownerService.getOwnerById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Получить всех владельцев")
    public ResponseEntity<List<OwnerResponseDto>> getAllOwners(
            @RequestParam(required = false) String search) {
        List<OwnerResponseDto> response;
        if (search != null && !search.trim().isEmpty()) {
            response = ownerService.search(search.trim());
        } else {
            response = ownerService.getAllOwners();
        }
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Обновить владельца")
    public ResponseEntity<OwnerResponseDto> updateOwner(
            @PathVariable Long id,
            @Valid @RequestBody OwnerRequestDto requestDto) {
        OwnerResponseDto response = ownerService.updateOwner(id, requestDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить владельца")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/pets")
    @Operation(summary = "Получить всех питомцев владельца")
    public ResponseEntity<List<PetResponseDto>> getOwnerPets(@PathVariable Long id) {
        List<PetResponseDto> response = ownerService.getOwnerPets(id);
        return ResponseEntity.ok(response);
    }
}


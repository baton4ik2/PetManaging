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
    
    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);
    private final OwnerService ownerService;
    
    @PostMapping
    @Operation(summary = "Создать нового владельца")
    public ResponseEntity<OwnerResponseDto> createOwner(@Valid @RequestBody OwnerRequestDto requestDto) {
        logger.info("Creating new owner: {} {}", requestDto.getFirstName(), requestDto.getLastName());
        OwnerResponseDto response = ownerService.createOwner(requestDto);
        logger.info("Owner created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить владельца по ID")
    public ResponseEntity<OwnerResponseDto> getOwnerById(@PathVariable Long id) {
        logger.debug("Getting owner by ID: {}", id);
        OwnerResponseDto response = ownerService.getOwnerById(id);
        logger.debug("Owner retrieved: {} {}", response.getFirstName(), response.getLastName());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Получить всех владельцев")
    public ResponseEntity<List<OwnerResponseDto>> getAllOwners(
            @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            logger.info("Searching owners with query: {}", search);
        } else {
            logger.debug("Getting all owners");
        }
        List<OwnerResponseDto> response;
        if (search != null && !search.trim().isEmpty()) {
            response = ownerService.search(search.trim());
        } else {
            response = ownerService.getAllOwners();
        }
        logger.debug("Found {} owners", response.size());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Обновить владельца")
    public ResponseEntity<OwnerResponseDto> updateOwner(
            @PathVariable Long id,
            @Valid @RequestBody OwnerRequestDto requestDto) {
        logger.info("Updating owner with ID: {}", id);
        OwnerResponseDto response = ownerService.updateOwner(id, requestDto);
        logger.info("Owner updated successfully: {} {}", response.getFirstName(), response.getLastName());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить владельца")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        logger.info("Deleting owner with ID: {}", id);
        ownerService.deleteOwner(id);
        logger.info("Owner deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/pets")
    @Operation(summary = "Получить всех питомцев владельца")
    public ResponseEntity<List<PetResponseDto>> getOwnerPets(@PathVariable Long id) {
        logger.debug("Getting pets for owner ID: {}", id);
        List<PetResponseDto> response = ownerService.getOwnerPets(id);
        logger.debug("Found {} pets for owner ID: {}", response.size(), id);
        return ResponseEntity.ok(response);
    }
}


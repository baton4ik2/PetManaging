package ru.akbirov.petproject.service;

import ru.akbirov.petproject.dto.PetRequestDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.PetType;

import java.util.List;

public interface PetService {
    
    PetResponseDto createPet(PetRequestDto requestDto);
    
    PetResponseDto getPetById(Long id);
    
    List<PetResponseDto> getAllPets();
    
    List<PetResponseDto> getPetsByType(PetType type);
    
    List<PetResponseDto> getPetsByOwnerId(Long ownerId);
    
    PetResponseDto updatePet(Long id, PetRequestDto requestDto);
    
    void deletePet(Long id);
    
    List<PetResponseDto> search(String searchTerm);
}

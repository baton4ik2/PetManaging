package ru.akbirov.petproject.service;

import ru.akbirov.petproject.dto.OwnerRequestDto;
import ru.akbirov.petproject.dto.OwnerResponseDto;
import ru.akbirov.petproject.dto.PetResponseDto;

import java.util.List;

public interface OwnerService {
    
    OwnerResponseDto createOwner(OwnerRequestDto requestDto);
    
    OwnerResponseDto getOwnerById(Long id);
    
    List<OwnerResponseDto> getAllOwners();
    
    OwnerResponseDto updateOwner(Long id, OwnerRequestDto requestDto);
    
    void deleteOwner(Long id);
    
    List<PetResponseDto> getOwnerPets(Long ownerId);
    
    List<OwnerResponseDto> search(String searchTerm);
}

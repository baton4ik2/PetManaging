package ru.akbirov.petproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.PetRequestDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.Pet;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.exception.OwnerNotFoundException;
import ru.akbirov.petproject.exception.PetNotFoundException;
import ru.akbirov.petproject.mapper.PetMapper;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.PetRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {
    
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
    private final PetMapper petMapper;
    
    @Transactional
    public PetResponseDto createPet(PetRequestDto requestDto) {
        Owner owner = ownerRepository.findById(requestDto.getOwnerId())
                .orElseThrow(() -> new OwnerNotFoundException(requestDto.getOwnerId()));
        
        Pet pet = petMapper.toEntity(requestDto);
        pet.setOwner(owner);
        
        Pet savedPet = petRepository.save(pet);
        return petMapper.toResponseDto(savedPet);
    }
    
    @Transactional(readOnly = true)
    public PetResponseDto getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(id));
        return petMapper.toResponseDto(pet);
    }
    
    @Transactional(readOnly = true)
    public List<PetResponseDto> getAllPets() {
        return petRepository.findAll().stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PetResponseDto> getPetsByType(PetType type) {
        return petRepository.findByType(type).stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PetResponseDto> getPetsByOwnerId(Long ownerId) {
        return petRepository.findByOwnerId(ownerId).stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PetResponseDto updatePet(Long id, PetRequestDto requestDto) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(id));
        
        Owner owner = ownerRepository.findById(requestDto.getOwnerId())
                .orElseThrow(() -> new OwnerNotFoundException(requestDto.getOwnerId()));
        
        pet.setName(requestDto.getName());
        pet.setType(requestDto.getType());
        pet.setBreed(requestDto.getBreed());
        pet.setDateOfBirth(requestDto.getDateOfBirth());
        pet.setColor(requestDto.getColor());
        pet.setDescription(requestDto.getDescription());
        pet.setOwner(owner);
        
        Pet updatedPet = petRepository.save(pet);
        return petMapper.toResponseDto(updatedPet);
    }
    
    @Transactional
    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new PetNotFoundException(id);
        }
        petRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<PetResponseDto> search(String searchTerm) {
        return petRepository.search(searchTerm).stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}


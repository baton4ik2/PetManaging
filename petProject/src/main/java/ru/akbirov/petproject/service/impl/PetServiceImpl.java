package ru.akbirov.petproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.PetRequestDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.Pet;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.entity.User;
import ru.akbirov.petproject.exception.OwnerNotFoundException;
import ru.akbirov.petproject.exception.PetNotFoundException;
import ru.akbirov.petproject.exception.UserNotFoundException;
import ru.akbirov.petproject.mapper.PetMapper;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.PetRepository;
import ru.akbirov.petproject.repository.UserRepository;
import ru.akbirov.petproject.service.PetService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {
    
    private static final Logger logger = LoggerFactory.getLogger(PetServiceImpl.class);
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final PetMapper petMapper;
    
    @Override
    @Transactional
    public PetResponseDto createPet(PetRequestDto requestDto) {
        logger.debug("Creating pet: {} (type: {}, ownerId: {})", 
                requestDto.getName(), requestDto.getType(), requestDto.getOwnerId());
        Owner owner = ownerRepository.findById(requestDto.getOwnerId())
                .orElseThrow(() -> {
                    logger.warn("Owner not found with ID: {}", requestDto.getOwnerId());
                    return new OwnerNotFoundException(requestDto.getOwnerId());
                });
        
        Pet pet = petMapper.toEntity(requestDto);
        pet.setOwner(owner);
        
        Pet savedPet = petRepository.save(pet);
        logger.info("Pet created successfully with ID: {}, name: {}, ownerId: {}", 
                savedPet.getId(), savedPet.getName(), savedPet.getOwner().getId());
        return petMapper.toResponseDto(savedPet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PetResponseDto getPetById(Long id) {
        logger.debug("Getting pet by ID: {}", id);
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pet not found with ID: {}", id);
                    return new PetNotFoundException(id);
                });
        logger.debug("Pet retrieved: {} (ID: {})", pet.getName(), pet.getId());
        return petMapper.toResponseDto(pet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDto> getAllPets() {
        logger.debug("Getting all pets");
        List<PetResponseDto> pets = petRepository.findAll().stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
        logger.debug("Retrieved {} pets", pets.size());
        return pets;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDto> getPetsByType(PetType type) {
        logger.debug("Getting pets by type: {}", type);
        List<PetResponseDto> pets = petRepository.findByType(type).stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
        logger.debug("Found {} pets of type: {}", pets.size(), type);
        return pets;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDto> getPetsByOwnerId(Long ownerId) {
        logger.debug("Getting pets by owner ID: {}", ownerId);
        List<PetResponseDto> pets = petRepository.findByOwnerId(ownerId).stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
        logger.debug("Found {} pets for owner ID: {}", pets.size(), ownerId);
        return pets;
    }
    
    @Override
    @Transactional
    public PetResponseDto updatePet(Long id, PetRequestDto requestDto) {
        logger.debug("Updating pet with ID: {}", id);
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pet not found with ID: {}", id);
                    return new PetNotFoundException(id);
                });
        
        Owner owner = ownerRepository.findById(requestDto.getOwnerId())
                .orElseThrow(() -> {
                    logger.warn("Owner not found with ID: {}", requestDto.getOwnerId());
                    return new OwnerNotFoundException(requestDto.getOwnerId());
                });
        
        pet.setName(requestDto.getName());
        pet.setType(requestDto.getType());
        pet.setBreed(requestDto.getBreed());
        pet.setDateOfBirth(requestDto.getDateOfBirth());
        pet.setColor(requestDto.getColor());
        pet.setDescription(requestDto.getDescription());
        pet.setOwner(owner);
        
        Pet updatedPet = petRepository.save(pet);
        logger.info("Pet updated successfully: {} (ID: {})", updatedPet.getName(), updatedPet.getId());
        return petMapper.toResponseDto(updatedPet);
    }
    
    @Override
    @Transactional
    public void deletePet(Long id) {
        logger.debug("Deleting pet with ID: {}", id);
        if (!petRepository.existsById(id)) {
            logger.warn("Pet not found with ID: {}", id);
            throw new PetNotFoundException(id);
        }
        petRepository.deleteById(id);
        logger.info("Pet deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDto> search(String searchTerm) {
        logger.debug("Searching pets with term: {}", searchTerm);
        List<PetResponseDto> pets = petRepository.search(searchTerm).stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
        logger.debug("Found {} pets matching search term: {}", pets.size(), searchTerm);
        return pets;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDto> getMyPets(String username) {
        logger.debug("Getting pets for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
        
        Owner owner = ownerRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    logger.warn("Owner not found for user ID: {}", user.getId());
                    return new OwnerNotFoundException("Owner not found for user: " + username);
                });
        
        List<PetResponseDto> pets = petRepository.findByOwnerId(owner.getId()).stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
        logger.debug("Found {} pets for user: {}", pets.size(), username);
        return pets;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPetOwner(Long petId, String username) {
        logger.debug("Checking if user {} is owner of pet {}", username, petId);
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> {
                    logger.warn("Pet not found with ID: {}", petId);
                    return new PetNotFoundException(petId);
                });
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
        
        Owner owner = ownerRepository.findByUserId(user.getId())
                .orElse(null);
        
        if (owner == null) {
            logger.debug("User {} has no owner profile", username);
            return false;
        }
        
        boolean isOwner = pet.getOwner().getId().equals(owner.getId());
        logger.debug("User {} is owner of pet {}: {}", username, petId, isOwner);
        return isOwner;
    }
}


package ru.akbirov.petproject.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.OwnerRequestDto;
import ru.akbirov.petproject.dto.OwnerResponseDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.exception.EmailAlreadyExistsException;
import ru.akbirov.petproject.exception.OwnerNotFoundException;
import ru.akbirov.petproject.mapper.OwnerMapper;
import ru.akbirov.petproject.mapper.PetMapper;
import ru.akbirov.petproject.repository.OwnerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerService {
    
    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final PetMapper petMapper;
    
    @Transactional
    public OwnerResponseDto createOwner(OwnerRequestDto requestDto) {
        logger.debug("Creating owner: {} {}, email: {}", 
                requestDto.getFirstName(), requestDto.getLastName(), requestDto.getEmail());
        if (ownerRepository.existsByEmail(requestDto.getEmail())) {
            logger.warn("Email already exists: {}", requestDto.getEmail());
            throw new EmailAlreadyExistsException(requestDto.getEmail());
        }
        
        Owner owner = ownerMapper.toEntity(requestDto);
        Owner savedOwner = ownerRepository.save(owner);
        logger.info("Owner created successfully with ID: {}, email: {}", 
                savedOwner.getId(), savedOwner.getEmail());
        return ownerMapper.toResponseDto(savedOwner);
    }
    
    @Transactional(readOnly = true)
    public OwnerResponseDto getOwnerById(Long id) {
        logger.debug("Getting owner by ID: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Owner not found with ID: {}", id);
                    return new OwnerNotFoundException(id);
                });
        OwnerResponseDto dto = ownerMapper.toResponseDto(owner);
        if (owner.getPets() != null) {
            dto.setPets(owner.getPets().stream()
                    .map(petMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }
        logger.debug("Owner retrieved: {} {} (ID: {}), pets count: {}", 
                owner.getFirstName(), owner.getLastName(), owner.getId(), 
                owner.getPets() != null ? owner.getPets().size() : 0);
        return dto;
    }
    
    @Transactional(readOnly = true)
    public List<OwnerResponseDto> getAllOwners() {
        logger.debug("Getting all owners");
        List<OwnerResponseDto> owners = ownerRepository.findAll().stream()
                .map(owner -> {
                    OwnerResponseDto dto = ownerMapper.toResponseDto(owner);
                    if (owner.getPets() != null) {
                        dto.setPets(owner.getPets().stream()
                                .map(petMapper::toResponseDto)
                                .collect(Collectors.toList()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        logger.debug("Retrieved {} owners", owners.size());
        return owners;
    }
    
    @Transactional
    public OwnerResponseDto updateOwner(Long id, OwnerRequestDto requestDto) {
        logger.debug("Updating owner with ID: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Owner not found with ID: {}", id);
                    return new OwnerNotFoundException(id);
                });
        
        if (!owner.getEmail().equals(requestDto.getEmail()) 
                && ownerRepository.existsByEmail(requestDto.getEmail())) {
            logger.warn("Email already exists: {}", requestDto.getEmail());
            throw new EmailAlreadyExistsException(requestDto.getEmail());
        }
        
        owner.setFirstName(requestDto.getFirstName());
        owner.setLastName(requestDto.getLastName());
        owner.setEmail(requestDto.getEmail());
        owner.setPhone(requestDto.getPhone());
        owner.setAddress(requestDto.getAddress());
        
        Owner updatedOwner = ownerRepository.save(owner);
        OwnerResponseDto dto = ownerMapper.toResponseDto(updatedOwner);
        if (updatedOwner.getPets() != null) {
            dto.setPets(updatedOwner.getPets().stream()
                    .map(petMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }
        logger.info("Owner updated successfully: {} {} (ID: {})", 
                updatedOwner.getFirstName(), updatedOwner.getLastName(), updatedOwner.getId());
        return dto;
    }
    
    @Transactional
    public void deleteOwner(Long id) {
        logger.debug("Deleting owner with ID: {}", id);
        if (!ownerRepository.existsById(id)) {
            logger.warn("Owner not found with ID: {}", id);
            throw new OwnerNotFoundException(id);
        }
        ownerRepository.deleteById(id);
        logger.info("Owner deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<PetResponseDto> getOwnerPets(Long ownerId) {
        logger.debug("Getting pets for owner ID: {}", ownerId);
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> {
                    logger.warn("Owner not found with ID: {}", ownerId);
                    return new OwnerNotFoundException(ownerId);
                });
        List<PetResponseDto> pets = owner.getPets().stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
        logger.debug("Retrieved {} pets for owner ID: {}", pets.size(), ownerId);
        return pets;
    }
    
    @Transactional(readOnly = true)
    public List<OwnerResponseDto> search(String searchTerm) {
        logger.debug("Searching owners with term: {}", searchTerm);
        List<OwnerResponseDto> owners = ownerRepository.search(searchTerm).stream()
                .map(owner -> {
                    OwnerResponseDto dto = ownerMapper.toResponseDto(owner);
                    if (owner.getPets() != null) {
                        dto.setPets(owner.getPets().stream()
                                .map(petMapper::toResponseDto)
                                .collect(Collectors.toList()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        logger.debug("Found {} owners matching search term: {}", owners.size(), searchTerm);
        return owners;
    }
}


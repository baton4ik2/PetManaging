package ru.akbirov.petproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.OwnerRequestDto;
import ru.akbirov.petproject.dto.OwnerResponseDto;
import ru.akbirov.petproject.dto.PetResponseDto;
import ru.akbirov.petproject.entity.Owner;
import ru.akbirov.petproject.entity.User;
import ru.akbirov.petproject.exception.AccessDeniedException;
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
    
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final PetMapper petMapper;
    private final UserService userService;
    
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }
    
    private void checkOwnerOwnership(Owner owner) {
        if (!isAdmin()) {
            User currentUser = userService.getCurrentUser();
            if (owner.getUser() == null || !owner.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only manage your own owner profile");
            }
        }
    }
    
    @Transactional
    public OwnerResponseDto createOwner(OwnerRequestDto requestDto) {
        if (ownerRepository.existsByEmail(requestDto.getEmail())) {
            throw new EmailAlreadyExistsException(requestDto.getEmail());
        }
        
        Owner owner = ownerMapper.toEntity(requestDto);
        
        // Если не админ, автоматически привязываем владельца к текущему пользователю
        if (!isAdmin()) {
            User currentUser = userService.getCurrentUser();
            owner.setUser(currentUser);
        }
        
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.toResponseDto(savedOwner);
    }
    
    @Transactional(readOnly = true)
    public OwnerResponseDto getOwnerById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException(id));
        OwnerResponseDto dto = ownerMapper.toResponseDto(owner);
        if (owner.getPets() != null) {
            dto.setPets(owner.getPets().stream()
                    .map(petMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
    
    @Transactional(readOnly = true)
    public List<OwnerResponseDto> getAllOwners() {
        return ownerRepository.findAll().stream()
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
    }
    
    @Transactional
    public OwnerResponseDto updateOwner(Long id, OwnerRequestDto requestDto) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException(id));
        
        // Проверяем права доступа
        checkOwnerOwnership(owner);
        
        if (!owner.getEmail().equals(requestDto.getEmail()) 
                && ownerRepository.existsByEmail(requestDto.getEmail())) {
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
        return dto;
    }
    
    @Transactional
    public void deleteOwner(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException(id));
        
        // Проверяем права доступа
        checkOwnerOwnership(owner);
        
        ownerRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<PetResponseDto> getOwnerPets(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new OwnerNotFoundException(ownerId));
        return owner.getPets().stream()
                .map(petMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OwnerResponseDto> search(String searchTerm) {
        return ownerRepository.search(searchTerm).stream()
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
    }
}


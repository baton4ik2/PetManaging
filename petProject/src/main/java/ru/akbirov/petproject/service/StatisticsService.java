package ru.akbirov.petproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.StatisticsDto;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.PetRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    
    @Transactional(readOnly = true)
    public StatisticsDto getStatistics() {
        long totalOwners = ownerRepository.count();
        long totalPets = petRepository.count();
        
        Map<String, Long> petsByType = Arrays.stream(PetType.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        type -> (long) petRepository.findByType(type).size()
                ));
        
        long averagePetsPerOwner = totalOwners > 0 ? totalPets / totalOwners : 0;
        
        return StatisticsDto.builder()
                .totalOwners(totalOwners)
                .totalPets(totalPets)
                .petsByType(petsByType)
                .averagePetsPerOwner(averagePetsPerOwner)
                .build();
    }
}


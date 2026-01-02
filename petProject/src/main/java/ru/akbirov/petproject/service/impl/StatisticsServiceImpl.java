package ru.akbirov.petproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akbirov.petproject.dto.StatisticsDto;
import ru.akbirov.petproject.entity.PetType;
import ru.akbirov.petproject.repository.OwnerRepository;
import ru.akbirov.petproject.repository.PetRepository;
import ru.akbirov.petproject.service.StatisticsService;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    
    @Override
    @Transactional(readOnly = true)
    public StatisticsDto getStatistics() {
        logger.debug("Calculating statistics");
        long totalOwners = ownerRepository.count();
        long totalPets = petRepository.count();
        
        Map<String, Long> petsByType = Arrays.stream(PetType.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        type -> (long) petRepository.findByType(type).size()
                ));
        
        long averagePetsPerOwner = totalOwners > 0 ? totalPets / totalOwners : 0;
        
        logger.debug("Statistics calculated: totalOwners={}, totalPets={}, averagePetsPerOwner={}", 
                totalOwners, totalPets, averagePetsPerOwner);
        
        return StatisticsDto.builder()
                .totalOwners(totalOwners)
                .totalPets(totalPets)
                .petsByType(petsByType)
                .averagePetsPerOwner(averagePetsPerOwner)
                .build();
    }
}


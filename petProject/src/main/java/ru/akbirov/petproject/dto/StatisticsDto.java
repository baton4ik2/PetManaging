package ru.akbirov.petproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    
    private Long totalOwners;
    private Long totalPets;
    private Map<String, Long> petsByType;
    private Long averagePetsPerOwner;
}


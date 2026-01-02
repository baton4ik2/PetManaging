package ru.akbirov.petproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akbirov.petproject.dto.StatisticsDto;
import ru.akbirov.petproject.service.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "API для получения статистики")
public class StatisticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);
    private final StatisticsService statisticsService;
    
    @GetMapping
    @Operation(summary = "Получить общую статистику")
    public ResponseEntity<StatisticsDto> getStatistics() {
        logger.info("Getting statistics");
        StatisticsDto statistics = statisticsService.getStatistics();
        logger.info("Statistics retrieved: totalOwners={}, totalPets={}", 
                statistics.getTotalOwners(), statistics.getTotalPets());
        return ResponseEntity.ok(statistics);
    }
}


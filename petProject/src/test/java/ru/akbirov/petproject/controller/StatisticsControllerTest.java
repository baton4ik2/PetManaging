package ru.akbirov.petproject.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.akbirov.petproject.dto.StatisticsDto;
import ru.akbirov.petproject.service.StatisticsService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void testGetStatistics_Success() throws Exception {
        // Given
        Map<String, Long> petsByType = new HashMap<>();
        petsByType.put("DOG", 5L);
        petsByType.put("CAT", 3L);
        petsByType.put("BIRD", 2L);

        StatisticsDto statisticsDto = StatisticsDto.builder()
                .totalOwners(10L)
                .totalPets(10L)
                .averagePetsPerOwner(1L)
                .petsByType(petsByType)
                .build();

        when(statisticsService.getStatistics()).thenReturn(statisticsDto);

        // When & Then
        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOwners").value(10L))
                .andExpect(jsonPath("$.totalPets").value(10L))
                .andExpect(jsonPath("$.averagePetsPerOwner").value(1L))
                .andExpect(jsonPath("$.petsByType.DOG").value(5L))
                .andExpect(jsonPath("$.petsByType.CAT").value(3L))
                .andExpect(jsonPath("$.petsByType.BIRD").value(2L));
    }
}


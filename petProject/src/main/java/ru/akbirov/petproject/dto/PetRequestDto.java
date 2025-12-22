package ru.akbirov.petproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.akbirov.petproject.entity.PetType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetRequestDto {
    
    @NotBlank(message = "Имя питомца обязательно")
    private String name;
    
    @NotNull(message = "Тип питомца обязателен")
    private PetType type;
    
    @NotBlank(message = "Порода обязательна")
    private String breed;
    
    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate dateOfBirth;
    
    private String color;
    
    private String description;
    
    @NotNull(message = "ID владельца обязателен")
    private Long ownerId;
}


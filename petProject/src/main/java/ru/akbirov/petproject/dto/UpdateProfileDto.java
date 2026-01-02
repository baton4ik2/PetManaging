package ru.akbirov.petproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDto {
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть валидным")
    private String email;
    
    @NotBlank(message = "Имя обязательно")
    private String firstName;
    
    @NotBlank(message = "Фамилия обязательна")
    private String lastName;
    
    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+7 \\d{3} \\d{3} \\d{2} \\d{2}$", message = "Номер телефона должен быть в формате +7 XXX XXX XX XX")
    private String phone;
}


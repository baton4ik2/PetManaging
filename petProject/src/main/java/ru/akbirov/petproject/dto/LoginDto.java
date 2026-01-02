package ru.akbirov.petproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    
    @NotBlank(message = "Имя пользователя обязательно")
    private String username;
    
    @NotBlank(message = "Пароль обязателен")
    private String password;
}


package ru.akbirov.petproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть валидным")
    private String email;
    
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String password;
    
    @NotBlank(message = "Имя обязательно")
    private String firstName;
    
    @NotBlank(message = "Фамилия обязательна")
    private String lastName;
    
    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+7 \\d{3} \\d{3} \\d{2} \\d{2}$", message = "Номер телефона должен быть в формате +7 XXX XXX XX XX")
    private String phone;
}


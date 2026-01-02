package ru.akbirov.petproject.service;

import ru.akbirov.petproject.dto.AuthResponseDto;
import ru.akbirov.petproject.dto.LoginDto;
import ru.akbirov.petproject.dto.RegisterDto;

public interface AuthService {
    
    AuthResponseDto register(RegisterDto registerDto);
    
    AuthResponseDto login(LoginDto loginDto);
}


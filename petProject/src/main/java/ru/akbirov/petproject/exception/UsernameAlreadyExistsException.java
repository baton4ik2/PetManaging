package ru.akbirov.petproject.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    
    public UsernameAlreadyExistsException(String username) {
        super("Username already exists: " + username);
    }
}


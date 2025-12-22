package ru.akbirov.petproject.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " уже используется");
    }
}


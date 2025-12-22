package ru.akbirov.petproject.exception;

public class PetNotFoundException extends RuntimeException {
    
    public PetNotFoundException(String message) {
        super(message);
    }
    
    public PetNotFoundException(Long id) {
        super("Питомец с ID " + id + " не найден");
    }
}


package ru.akbirov.petproject.exception;

public class OwnerNotFoundException extends RuntimeException {
    
    public OwnerNotFoundException(String message) {
        super(message);
    }
    
    public OwnerNotFoundException(Long id) {
        super("Владелец с ID " + id + " не найден");
    }
}


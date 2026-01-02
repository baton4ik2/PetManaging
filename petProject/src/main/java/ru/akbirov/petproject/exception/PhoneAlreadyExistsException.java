package ru.akbirov.petproject.exception;

public class PhoneAlreadyExistsException extends RuntimeException {
    
    public PhoneAlreadyExistsException(String phone) {
        super("Номер телефона " + phone + " уже используется");
    }
}



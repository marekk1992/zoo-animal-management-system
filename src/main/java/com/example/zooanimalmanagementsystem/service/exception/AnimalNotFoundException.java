package com.example.zooanimalmanagementsystem.service.exception;

public class AnimalNotFoundException extends RuntimeException{

    public AnimalNotFoundException(String message) {
        super(message);
    }
}

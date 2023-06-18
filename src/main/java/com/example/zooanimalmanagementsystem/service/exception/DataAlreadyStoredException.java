package com.example.zooanimalmanagementsystem.service.exception;

public class DataAlreadyStoredException extends RuntimeException{

    public DataAlreadyStoredException(String message) {
        super(message);
    }
}

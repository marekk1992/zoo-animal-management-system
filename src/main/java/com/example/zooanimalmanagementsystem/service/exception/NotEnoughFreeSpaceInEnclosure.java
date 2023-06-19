package com.example.zooanimalmanagementsystem.service.exception;

public class NotEnoughFreeSpaceInEnclosure extends RuntimeException{

    public NotEnoughFreeSpaceInEnclosure(String message) {
        super(message);
    }
}

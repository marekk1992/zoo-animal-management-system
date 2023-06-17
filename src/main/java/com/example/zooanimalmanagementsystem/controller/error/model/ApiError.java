package com.example.zooanimalmanagementsystem.controller.error.model;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiError(HttpStatus httpStatus, String message, LocalDateTime localDateTime) {

}

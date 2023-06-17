package com.example.zooanimalmanagementsystem.controller.error;

import com.example.zooanimalmanagementsystem.controller.error.model.ApiError;
import com.example.zooanimalmanagementsystem.service.exception.InputFileNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ZooExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleException(Exception exc) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleException(InputFileNotAvailableException exc) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, exc.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}

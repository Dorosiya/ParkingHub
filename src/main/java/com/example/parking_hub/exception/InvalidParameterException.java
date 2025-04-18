package com.example.parking_hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends RuntimeException {
    
    public InvalidParameterException(String message) {
        super(message);
    }
    
    public InvalidParameterException(String paramName, String reason) {
        super(String.format("Invalid parameter '%s': %s", paramName, reason));
    }
} 
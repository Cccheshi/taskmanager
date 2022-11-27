package com.example.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class NotAllowedActionException extends RuntimeException {
    public NotAllowedActionException(String message) {
        super(message);
    }
}

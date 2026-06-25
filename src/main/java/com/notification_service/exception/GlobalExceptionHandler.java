package com.notification_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, String>> handleMailException(
            MailException exception){
        Map<String, String> errorResponse = Map.of(
                "message", "Email could not be sent. Please try again later."
        );
    return new ResponseEntity<>(
            errorResponse, HttpStatus.INTERNAL_SERVER_ERROR
    );
    }

}

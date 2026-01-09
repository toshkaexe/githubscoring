package com.github.scoring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GithubValidationException.class)
    public ResponseEntity<Map<String, Object>> handleGithubValidationException(GithubValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(createErrorResponse(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        "Validation Failed",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(GithubServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleGithubServiceUnavailableException(GithubServiceUnavailableException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Service Unavailable",
                        ex.getMessage()
                ));
    }


    private Map<String, Object> createErrorResponse(int status, String error, String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "error", error,
                "message", message
        );
    }
}
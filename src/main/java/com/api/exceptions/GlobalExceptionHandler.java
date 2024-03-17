package com.api.exceptions;

import com.api.component.CustomLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final CustomLogger customLogger;

    public GlobalExceptionHandler(CustomLogger customLogger) {
        this.customLogger = customLogger;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        customLogger.logError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error occurred");
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        customLogger.logError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter '" + paramName + "' is missing");
    }
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleClientError(HttpClientErrorException ex) {
        customLogger.logError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Client error occurred");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        // Обработка ошибки 405 (Method Not Allowed)
        customLogger.logError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Method Not Allowed");
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleServerError(HttpServerErrorException ex) {
        // Обработка ошибки 500 (Internal Server Error)
        customLogger.logError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error occurred");
    }
}

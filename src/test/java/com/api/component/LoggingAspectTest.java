package com.api.component;

import com.api.component.CustomLogger;
import com.api.exceptions.ErrorResponse;
import com.api.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private CustomLogger customLogger;
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        customLogger = mock(CustomLogger.class);
        globalExceptionHandler = new GlobalExceptionHandler(customLogger);
    }

    @Test
    void handleInternalServerError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Internal Server Error");

        // Act
        ErrorResponse response = globalExceptionHandler.handleInternalServerError(exception);

        // Assert
        assertEquals("Internal Server Error", response.getMessage());
        verify(customLogger).logError("error, 500 code");
    }

    @Test
    void handleBadRequestException() {
        // Arrange
        Exception exception = new MissingServletRequestParameterException("paramName", "paramType");

        // Act
        ErrorResponse response = globalExceptionHandler.handleBadRequestException(exception);

        // Assert
        assertEquals("400 error, BAD REQUEST", response.getMessage());
        verify(customLogger).logError("error, 400 code");
    }

    @Test
    void handleMethodNotAllowed() {
        // Arrange
        Exception exception = new HttpRequestMethodNotSupportedException("GET");

        // Act
        ErrorResponse response = globalExceptionHandler.handleMethodNotAllowed(exception);

        // Assert
        assertEquals("405 error, METHOD NOT ALLOWED", response.getMessage());
        verify(customLogger).logError("error, 405 code");
    }

    @Test
    void handleNotFoundException() {
        // Arrange
        Exception exception = new NoHandlerFoundException("GET", "/api/test", null);

        // Act
        ErrorResponse response = globalExceptionHandler.handlerFoundException(exception);

        // Assert
        assertEquals("404 error, NOT FOUND", response.getMessage());
        verify(customLogger).logError("error, 404 code");
    }
}

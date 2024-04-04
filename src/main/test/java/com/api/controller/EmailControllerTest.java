package com.api.controller;


import com.api.component.CustomLogger;
import com.api.dto.EmailDTO;
import com.api.service.EmailService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailControllerTest {
    @InjectMocks
    EmailController emailController;

    @Mock
    EmailService emailService;

    @Mock
    CustomLogger customLogger;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void processUrl() {
        // Arrange
        String inputText = "maeww@mail.com";
        String processedText = "";
        EmailDTO expectedDto = new EmailDTO(processedText);

        when(emailService.getConfidentialText(inputText)).thenReturn(processedText);

        // Act
        ResponseEntity<EmailDTO> response = emailController.processUrl(inputText);

        // Assert
        verify(customLogger).logInfo("POST : processUrl");
        assertEquals(expectedDto.getText(), response.getBody().getText());
    }

    @org.junit.jupiter.api.Test
    void add() {
    }

    @org.junit.jupiter.api.Test
    void getEmails() {
    }

    @org.junit.jupiter.api.Test
    void getEmailsByDomain() {
    }

    @org.junit.jupiter.api.Test
    void updateEmailById() {
    }

    @org.junit.jupiter.api.Test
    void updateEmailByName() {
    }

    @org.junit.jupiter.api.Test
    void deleteEmailById() {
    }

    @org.junit.jupiter.api.Test
    void deleteEmailByName() {
    }
}
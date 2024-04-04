package com.api.controller;

import com.api.component.CustomLogger;
import com.api.dto.EmailDTO;
import com.api.dto.MessageDTO;
import com.api.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailControllerTest {

    @Mock
    private CustomLogger logger;

    @Mock
    private EmailService service;

    @InjectMocks
    private EmailController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessUrl() {
        String inputText = "test";
        String processedText = "processedTest";
        EmailDTO emailDTO = new EmailDTO(processedText);
        when(service.getConfidentialText(inputText)).thenReturn(processedText);

        ResponseEntity<EmailDTO> response = controller.processUrl(inputText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(processedText, response.getBody().getText());
        verify(logger).logInfo("POST : processUrl");
    }

    @Test
    public void testGetEmails() {
        String text = "example text";
        List<EmailDTO> emailList = new ArrayList<>();
        when(service.getEmails(text)).thenReturn(emailList);

        ResponseEntity<List<EmailDTO>> response = controller.getEmails(text);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailList, response.getBody());
        verify(logger).logInfo("GET : getEmails");
    }

    @Test
    public void testGetEmailsByDomain() {
        String text = "example domain";
        List<EmailDTO> emailList = new ArrayList<>();
        when(service.getEmailsByEmailType(text)).thenReturn(emailList);

        ResponseEntity<List<EmailDTO>> response = controller.getEmailsByDomain(text);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emailList, response.getBody());
        verify(logger).logInfo("GET : getEmailsByDomain");
    }

    @Test
    public void testUpdateEmailById() {
        Long id = 1L;
        String newDomain = "newDomain";
        MessageDTO messageDTO = new MessageDTO("Success");
        ResponseEntity<MessageDTO> expectedResponse = ResponseEntity.ok(messageDTO);

        ResponseEntity<MessageDTO> response = controller.updateEmailById(id, newDomain);

        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        verify(logger).logInfo("PUT : updateEmailsById");
        verify(service).updateEmail(id, newDomain);
    }

    @Test
    public void testUpdateEmailByName() {
        String email = "old@example.com";
        String newEmail = "new@example.com";
        MessageDTO messageDTO = new MessageDTO("Success");
        ResponseEntity<MessageDTO> expectedResponse = ResponseEntity.ok(messageDTO);

        ResponseEntity<MessageDTO> response = controller.updateEmailByName(email, newEmail);

        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        verify(logger).logInfo("PUT : updateEmailsByName");
        verify(service).updateEmail(email, newEmail);
    }

    @Test
    public void testDeleteEmailById() {
        Long emailId = 1L;
        MessageDTO messageDTO = new MessageDTO("Email deleted successfully");
        ResponseEntity<MessageDTO> expectedResponse = ResponseEntity.ok(messageDTO);

        ResponseEntity<MessageDTO> response = controller.deleteEmailById(emailId);

        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        verify(logger).logInfo("DELETE : deleteEmailsById");
        verify(service).deleteEmail(emailId);
    }

    @Test
    public void testDeleteEmailByName() {
        String email = "test@example.com";
        MessageDTO messageDTO = new MessageDTO("Email deleted successfully");
        ResponseEntity<MessageDTO> expectedResponse = ResponseEntity.ok(messageDTO);

        ResponseEntity<MessageDTO> response = controller.deleteEmailByName(email);

        assertEquals(expectedResponse.getStatusCode(), response.getStatusCode());
        verify(logger).logInfo("DELETE : deleteEmailsByName");
        verify(service).deleteEmail(email);
    }
}

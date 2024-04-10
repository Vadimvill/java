package com.api.controller;

import com.api.component.CustomLogger;
import com.api.dto.EmailDTO;
import com.api.dto.MessageDTO;
import com.api.service.EmailService;
import com.api.service.RequestCounterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmailController {
    private static final String SUCCESS_MSG = "Success";
    private final CustomLogger logger;
    private final EmailService service;
    private final RequestCounterService requestCounterService;

    public EmailController(CustomLogger logger, EmailService service, RequestCounterService requestCounterService) {
        this.logger = logger;
        this.service = service;
        this.requestCounterService = requestCounterService;
    }

    @PostMapping("/processUrl")
    public ResponseEntity<EmailDTO> processUrl(@RequestParam String text) {
        logger.logInfo("POST : processUrl");
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        String processedText = service.getConfidentialText(text);
        EmailDTO dto = new EmailDTO(processedText);
        if (dto.getText() == null) {
            dto.setText("null");
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/add")
    public ResponseEntity<MessageDTO> add(@RequestBody List<EmailDTO> email) {
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        service.addEmails(email);
        return new ResponseEntity<>(new MessageDTO("all ok"), HttpStatus.OK);
    }

    @GetMapping("/getEmails")
    public ResponseEntity<List<EmailDTO>> getEmails(@RequestParam String text) {
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        logger.logInfo("GET : getEmails");
        List<EmailDTO> emailList = service.getEmails(text);
        return ResponseEntity.ok(emailList);
    }

    @GetMapping("/getEmailsByDomain")
    public ResponseEntity<List<EmailDTO>> getEmailsByDomain(@RequestParam String text) {
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        logger.logInfo("GET : getEmailsByDomain");
        List<EmailDTO> emailList = service.getEmailsByEmailType(text);
        return ResponseEntity.ok(emailList);
    }

    @PutMapping("/updateEmailById")
    public ResponseEntity<MessageDTO> updateEmailById(@RequestParam Long id, String newDomain) {
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        logger.logInfo("PUT : updateEmailsById");
        service.updateEmail(id, newDomain);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @PutMapping("/updateEmailByName")
    public ResponseEntity<MessageDTO> updateEmailByName(@RequestParam String email, String newEmail) {
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        logger.logInfo("PUT : updateEmailsByName");
        service.updateEmail(email, newEmail);
        return ResponseEntity.ok(new MessageDTO(SUCCESS_MSG));
    }

    @DeleteMapping("/deleteEmailById")
    public ResponseEntity<MessageDTO> deleteEmailById(@RequestParam Long emailId) {
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        logger.logInfo("DELETE : deleteEmailsById");
        service.deleteEmail(emailId);
        return ResponseEntity.ok(new MessageDTO("Email deleted successfully"));
    }

    @DeleteMapping("/deleteEmailByName")
    public ResponseEntity<MessageDTO> deleteEmailByName(@RequestParam String email) {
        logger.logInfo(String.valueOf(requestCounterService.incrementAndGet()));
        logger.logInfo("DELETE : deleteEmailsByName");
        service.deleteEmail(email);
        return ResponseEntity.ok(new MessageDTO("Email deleted successfully"));
    }
}
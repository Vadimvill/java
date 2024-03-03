package com.api.controller;
import com.api.dto.DomainDTO;
import com.api.dto.EmailDTO;
import com.api.dto.MessageDTO;
import com.api.service.ProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.List;



@RestController
public class ProcessController {
    private final ProcessService service;
    public ProcessController(ProcessService service) {
        this.service = service;
    }
    @PostMapping("/processUrl")
    public ResponseEntity<EmailDTO> processUrl(@RequestParam String text) {
        try {
            String processedText = service.getConfidentialText(text);
            EmailDTO dto = new EmailDTO(processedText);
            if (dto.getText() == null) dto.setText("null");
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
          
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EmailDTO("Error processing text"));
        }
    }

    @GetMapping("/getEmails")
    public ResponseEntity<List<EmailDTO>> getEmails(@RequestParam String text) {
        try {
            List<EmailDTO> emailList = service.getEmails(text);
            return ResponseEntity.ok(emailList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @GetMapping("/getDomains")
    public ResponseEntity<List<DomainDTO>> getDomains() {
        try {
            List<DomainDTO> list = service.getDomains();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    @PutMapping("/updateEmailById")
    public ResponseEntity<MessageDTO> updateEmailById(Long id, String newEmail) {
       if(service.updateEmail(id,newEmail)){
           return ResponseEntity.ok(new MessageDTO("Success"));
       }
       else return ResponseEntity.ok(new MessageDTO("Failed"));
    }
    @PutMapping("/updateEmailByName")
    public ResponseEntity<MessageDTO> updateEmailByName(String email,String newEmail) {
        if(service.updateEmail(email,newEmail)){
            return ResponseEntity.ok(new MessageDTO("Success"));
        }
        else return ResponseEntity.ok(new MessageDTO("Failed"));
    }
    @DeleteMapping("/deleteEmailById")
    public ResponseEntity<MessageDTO> deleteEmailById(@RequestParam Long emailId) {
        try {
            service.deleteEmail(emailId);
            return ResponseEntity.ok(new MessageDTO("Email deleted successfully"));
        } catch (Exception e) {
        
            return ResponseEntity.ok(new MessageDTO("Email deleted fail"));
        }
    }
    @DeleteMapping("/deleteDomainById")
    public ResponseEntity<MessageDTO> deleteDomainById(@RequestParam Long domainId) {
        try {
            service.deleteDomain(domainId);
            return ResponseEntity.ok(new MessageDTO("Domain deleted successfully"));
        } catch (Exception e) {
          
            return ResponseEntity.ok(new MessageDTO("Domain deleted fail"));
        }
    }
    @DeleteMapping("/deleteDomainByName")
    public ResponseEntity<MessageDTO> deleteDomainByName(@RequestParam String domain) {
        try {
            service.deleteDomain(domain);
            return ResponseEntity.ok(new MessageDTO("Domain deleted successfully"));
        } catch (Exception e) {
          
            return ResponseEntity.ok(new MessageDTO("Domain deleted fail"));
        }
    }
    @DeleteMapping("/deleteEmailByName")
    public ResponseEntity<MessageDTO> deleteEmailByName(@RequestParam String email) {
        try {
            service.deleteEmail(email);
            return ResponseEntity.ok(new MessageDTO("Email deleted successfully"));
        } catch (Exception e) {
        
            return ResponseEntity.ok(new MessageDTO("Email deleted fail"));
        }
    }



}

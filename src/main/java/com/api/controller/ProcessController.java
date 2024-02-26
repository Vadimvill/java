package com.api.controller;
import com.api.dto.DTO;
import com.api.service.ProcessService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ProcessController {
    private final ProcessService service;

    public ProcessController(ProcessService service) {
        this.service = service;
    }
    @GetMapping("/processUrl")
    public DTO processUrl(@RequestParam String text) {
        text = service.getSecureText(text);
        DTO dto = new DTO(text);
        if(dto.getText() == null) dto.setText("null");
        return dto;
    }
}
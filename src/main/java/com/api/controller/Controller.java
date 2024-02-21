package com.api.controller;

import com.api.service.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }
    @GetMapping("/processUrl")
    public DTO processUrl(@RequestParam String text) {
        text = service.getSecureText(text);

        DTO dto = new DTO(text);
        if(dto.text == null) dto.setText("null");
        return dto;
    }
}
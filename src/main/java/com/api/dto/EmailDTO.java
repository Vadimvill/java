package com.api.dto;

public class EmailDTO {
    String text;

    public EmailDTO(String text) {
        this.text = text;
    }

    public EmailDTO() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

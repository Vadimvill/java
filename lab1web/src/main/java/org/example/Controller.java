package org.example;

import org.example.Getter;
import org.example.Service;

import java.io.IOException;

public class Controller{
    DTO getSecureText(String text) throws IOException {
        Getter getter = new Service();
        return new DTO(getter.getSecureText(text));

    }
}
class DTO{
    private String value;
    public String getValue() {
        return value;
    }
public DTO(String value){
        this.value = value;
}

    public void setValue(String value) {
        this.value = value;
    }
}

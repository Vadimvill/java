package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Service implements  Getter{
    public String getSecureText(String text) throws DataBaseRuntimeException {
        List<String> list = new ArrayList<>();
        String phoneRegex = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?){1,2}\\d{1,9}\\b";
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher phoneMatcher = phonePattern.matcher(text);
        text = phoneMatcher.replaceAll("");

        String emailRegex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b";

        Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(text);
        while (emailMatcher.find()){
            list.add(emailMatcher.group());
        }
        text = emailMatcher.replaceAll("");



        return text;
    }
}
class DataBaseRuntimeException extends RuntimeException{
    final String text;
    public  DataBaseRuntimeException(String text){
        this.text = text;
    }
}

package com.api.service;

import com.api.dao.EmailDao;
import com.api.dao.EmailEntity;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@org.springframework.stereotype.Service
@ComponentScan(basePackages = "com.api.dao")
@EnableJpaRepositories(basePackages = "com.api.dao")
@EntityScan("com.api.dao")
public class Service{
    private final EmailDao emailDao;
    public Service(EmailDao emailDao){
        this.emailDao = emailDao;
    }
public String getSecureText(String text){
    List<String> list = new ArrayList<>();
    String phoneRegex = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?){1,2}\\d{1,9}\\b";
    Pattern phonePattern = Pattern.compile(phoneRegex);
    Matcher phoneMatcher = phonePattern.matcher(text);
    text = phoneMatcher.replaceAll("");

    String emailRegex = "\\b[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}\\b";

    Pattern emailPattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
    Matcher emailMatcher = emailPattern.matcher(text);
    while (emailMatcher.find()){
        list.add(emailMatcher.group());
    }
    text = emailMatcher.replaceAll("");

    for(int i = 0;i<list.size();i++){
        emailDao.saveEntity(new EmailEntity(list.get(i)));
    }
    return text;

  }
}

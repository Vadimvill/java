package com.api.service;

import com.api.component.Cache;
import com.api.component.CustomLogger;
import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    EmailTypeRepository emailTypeRepository;

    @Mock
    Cache cache;

    @Mock
    EmailRepository emailRepository;

    @Mock
    CustomLogger customLogger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getConfidentinalText(){
        String text1 = "thinde@mail.ru test +375202556867 ms";
        String text2 = "t+375202556867 ms";
        String text3 = "thinde@mail.ru ms";
        String text4 = "teterte@mail.com";
        String result1 = emailService.getConfidentialText(text1);
        String result2 = emailService.getConfidentialText(text2);
        String result3 = emailService.getConfidentialText(text3);
        String result4 = emailService.getConfidentialText(text4);
        Assertions.assertEquals(" test + ms",result1);
        Assertions.assertEquals("t ms",result2);
        Assertions.assertEquals(" ms",result3);
        Assertions.assertEquals("",result4);
    }

}

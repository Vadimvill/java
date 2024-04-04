package com.api.service;

import com.api.component.Cache;
import com.api.component.CustomLogger;
import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import com.api.dto.EmailDTO;
import com.api.entity.Email;
import com.api.entity.EmailType;
import com.api.exceptions.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;

import java.util.ArrayList;
import java.util.List;


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
    void shouldReturnAllEmails() {
        Mockito.when(emailRepository.findAll()).thenReturn(getEmails());
        List<EmailDTO> res = emailService.getEmails("all");
        Assertions.assertEquals(3,res.size());
        Assertions.assertNotNull(res.get(0));
    }

    private List<Email> getEmails(){
        List<Email> list = new ArrayList<>();
        list.add(new Email(1L,"vafda@gmail.com"));
        list.add(new Email(2L,"vafrteda@mail.com"));
        list.add(new Email(3L,"vafda@rambler.ru"));
        return list;
    }
}



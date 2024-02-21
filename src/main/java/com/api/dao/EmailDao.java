package com.api.dao;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailDao {
    private final EmailRepository emailRepository;


    public EmailDao(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }
    @Transactional
    public EmailEntity saveEntity(EmailEntity emailEntity){
        return emailRepository.save(emailEntity);
    }
}

package com.api.dao;
import com.api.entity.EmailEntity;
import com.api.entity.EmailTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
    EmailEntity findByEmail(String email);
    List<EmailEntity> findByEmailTypeEntity(EmailTypeEntity emailType);
}

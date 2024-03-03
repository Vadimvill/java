package com.api.dao;

import com.api.entity.EmailTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailIdRepository extends JpaRepository<EmailTypeEntity,Long> {
    EmailTypeEntity findByDomain(String emailType);
}

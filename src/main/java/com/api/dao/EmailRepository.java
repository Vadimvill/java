package com.api.dao;
import com.api.entity.Email;
import com.api.entity.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    Email findByName(String email);
    List<Email> findByTypeEmail(EmailType emailType);
}

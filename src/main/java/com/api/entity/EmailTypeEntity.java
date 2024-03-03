package com.api.entity;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "emails_type")
public class EmailTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_email_id")
    private Long typeEmailId;

    @Column(name = "domain")
    private String domain;

    @OneToMany(mappedBy = "emailTypeEntity", cascade = CascadeType.ALL)
    private List<EmailEntity> emails;

    public EmailTypeEntity(String domain) {
        this.domain = domain;
    }

    public EmailTypeEntity() {
    }

    public EmailTypeEntity(String domain, List<EmailEntity> emails) {
        this.domain = domain;
        this.emails = emails;
    }

    public Long getTypeEmailId() {
        return typeEmailId;
    }

    public void setTypeEmailId(Long typeEmailId) {
        this.typeEmailId = typeEmailId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<EmailEntity> getEmails() {
        return emails;
    }

    public void setEmails(List<EmailEntity> emails) {
        this.emails = emails;
    }
}
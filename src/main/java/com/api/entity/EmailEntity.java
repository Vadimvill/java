package com.api.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "emails")
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "type_email_id", nullable = false)
    private EmailTypeEntity emailTypeEntity;

    public EmailEntity(String email, EmailTypeEntity emailTypeEntity) {
        this.email = email;
        this.emailTypeEntity = emailTypeEntity;
    }

    public EmailEntity() {
    }

    public EmailEntity(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailTypeEntity getEmailTypeEntity() {
        return emailTypeEntity;
    }

    public void setEmailTypeEntity(EmailTypeEntity emailTypeEntity) {
        this.emailTypeEntity = emailTypeEntity;
    }
}


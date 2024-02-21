package com.api.dao;


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

    public EmailEntity() {

    }

    public Long getId() {
        return id;
    }

    public EmailEntity(String email) {
        this.email = email;
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

    }


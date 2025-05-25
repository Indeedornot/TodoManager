package com.bmisiek.todomanager.security.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    private String username;

    @Setter
    private String password;

    @Setter
    private String role;

    public User() {

    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }
}
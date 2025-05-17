package com.uvaXplore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users") // Change the table name to "users" instead of "user"
public class User {

    @Id
    private String enrollmentNumber;
    private String name;
    private String email;
    private String role;
    private String phoneNumber;
    private String password;

    @Column(name = "is_restricted")
    private Boolean isRestricted = false;
}

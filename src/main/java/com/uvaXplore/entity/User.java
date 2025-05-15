package com.uvaXplore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private String enrollmentNumber;
    private String name;
    private String email;
    private String role;
    private String phoneNumber;
    private String passowrd;
}

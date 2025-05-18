package com.uvaXplore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer facultyId;

    private String name;
}

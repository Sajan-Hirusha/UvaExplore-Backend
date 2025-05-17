package com.uvaXplore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int courseId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "degree_id", nullable = false)
    private Degree degree;
}

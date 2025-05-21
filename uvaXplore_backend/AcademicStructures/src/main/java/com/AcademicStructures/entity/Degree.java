package com.AcademicStructures.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Degree {
    @Id
    private String degreeId; // Changed to match your Course entity
    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}


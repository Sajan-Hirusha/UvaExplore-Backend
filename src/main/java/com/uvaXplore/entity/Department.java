package com.uvaXplore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Department {

    @Id
    private int departmentId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}

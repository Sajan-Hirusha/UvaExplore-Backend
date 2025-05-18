package com.uvaXplore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Faculty {

    @Id
    private Integer facultyId;

    private String name;
}

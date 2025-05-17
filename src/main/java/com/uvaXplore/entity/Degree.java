package com.uvaXplore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Degree {

    @Id
    private String degreeId;

    private String name;
}

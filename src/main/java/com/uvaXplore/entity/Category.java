package com.uvaXplore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Category {

    @Id
    private int categoryId;

    private String name;

    private String description;
}

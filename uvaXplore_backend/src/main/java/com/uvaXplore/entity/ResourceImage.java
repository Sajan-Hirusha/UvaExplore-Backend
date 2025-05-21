package com.AcademicStructures.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ResourceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String url;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;
}

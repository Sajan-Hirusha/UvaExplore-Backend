package com.Resource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ResourceContributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private String userEnrollmentNumber; // Reference to User
    private String userName; // Denormalized for display
}

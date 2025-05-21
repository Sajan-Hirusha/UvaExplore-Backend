package com.Resource.entity;

//import com.uvaXplore.middleware.VectorConverter;
import com.Resource.middleware.VectorType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    private String title;
    private String courseId;  // Reference to Course in AcademicStructure
    private String courseName; // Denormalized for display
    private String degreeId;  // Reference to Degree in AcademicStructure
    private String degreeName; // Denormalized for display
    private Integer categoryId; // Reference to Category in AcademicStructure
    private String categoryName; // Denormalized for display

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @Column(length = 3000)
    private String abstractText;

    private String documentPath;
    private Boolean isVerified;
    private LocalDateTime uploadAt;
    private String publication;
    private String googleDocLink;
    private String githubLink;
    private String sourceCodePath;

    @Type(VectorType.class)
    @Column(columnDefinition = "vector(384)")
    private float[] embedding;

    // User references
    private String supervisorEnrollment;
    private String supervisorName;
    private String coSupervisorEnrollment;
    private String coSupervisorName;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceContributor> contributors = new ArrayList<>();

    public enum ResourceType {
        Research,
        Project
    }
}
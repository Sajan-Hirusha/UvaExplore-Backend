package com.AcademicStructures.entity;

import com.pgvector.PGvector;
import com.AcademicStructures.middleware.FloatArrayToStringConverter;
//import com.uvaXplore.middleware.VectorConverter;
import com.AcademicStructures.middleware.VectorType;
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

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "degree_id")
    private Degree degree;

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


    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceContributor> contributors = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "supervisor_enrollment", referencedColumnName = "enrollmentNumber")
    private User supervisor;

    @ManyToOne
    @JoinColumn(name = "co_supervisor_enrollment", referencedColumnName = "enrollmentNumber")
    private User coSupervisor;

    public enum ResourceType {
        Research,
        Project
    }
}

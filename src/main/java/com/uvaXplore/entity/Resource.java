package com.uvaXplore.entity;

import com.uvaXplore.middleware.FloatArrayToStringConverter;
import jakarta.persistence.*;
import lombok.Data;

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

    private String courseId;

    @Enumerated(EnumType.STRING)
    private ResourceType type;

    private String categoryId;

    private String degree;

    @Column(length = 3000)
    private String abstractText;

    private String documentPath;

    private Boolean isVerified;

    private LocalDateTime uploadAt;

    private String publication;

    private String googleDocLink;

    private String sourceCodePath;

    @Convert(converter = FloatArrayToStringConverter.class)
    @Column(columnDefinition = "vector(384)")
    private float[] embedding;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceContributor> contributors = new ArrayList<>();

    public enum ResourceType {
        Research,
        Project
    }
}

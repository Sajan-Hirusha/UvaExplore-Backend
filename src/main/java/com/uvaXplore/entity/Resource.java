package com.uvaXplore.entity;

import com.uvaXplore.middleware.FloatArrayToStringConverter;
import com.uvaXplore.middleware.StringArrayConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    private String title;

    @Convert(converter = StringArrayConverter.class)
    @Column(length = 1000)
    private String[] imageUrl;

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

    public enum ResourceType {
        Research,
        Project
    }
}

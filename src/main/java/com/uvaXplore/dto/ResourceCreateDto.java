package com.uvaXplore.dto;

import com.uvaXplore.entity.Resource;
import lombok.Data;

import java.util.List;

@Data
public class ResourceCreateDto {
    private String title;
    private String courseId;
    private Integer categoryId;
    private String degreeId;
    private Resource.ResourceType type;
    private String abstractText;
    private String publication;
    private String googleDocLink;
    private String documentPath;
    private List<String> imageUrls;
    private List<ContributorDto> contributors;
    private String supervisorEnrollment;
    private String coSupervisorEnrollment;
    private String githubLink;

}
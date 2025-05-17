package com.uvaXplore.dto;

import com.uvaXplore.entity.Resource;
import lombok.Data;

import java.util.List;

@Data
public class ResourceCreateDto {
    private String title;
    private String courseId;
    private Resource.ResourceType type;
    private Integer categoryId;
    private String degreeId;
    private String abstractText;
    private String publication;
    private String googleDocLink;
    private String documentUrl;
    private List<String> imageUrls;
    private List<ContributorDto> contributors;
    private String supervisorEnrollment;
    private String coSupervisorEnrollment;

}
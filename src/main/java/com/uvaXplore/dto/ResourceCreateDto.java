package com.uvaXplore.dto;

import com.uvaXplore.entity.Resource;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ResourceCreateDto {
    private String title;
    private String courseId;
    private Resource.ResourceType type;
    private String categoryId;
    private String degree;
    private String abstractText;
    private String publication;
    private String googleDocLink;
    private MultipartFile pdfFile;
    private List<MultipartFile> images;
    private List<ContributorDto> contributors;
    private SupervisorDto supervisor;
    private SupervisorDto coSupervisor;
}
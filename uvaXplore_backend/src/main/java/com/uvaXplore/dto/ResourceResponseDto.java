package com.AcademicStructures.dto;

import com.AcademicStructures.entity.Resource;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResourceResponseDto {
    private Long resourceId;
    private String title;
    private String courseId;
    private Resource.ResourceType type;
    private String categoryId;
    private String degree;
    private String abstractText;
    private String documentUrl;
    private List<String> imageUrls;
    private String publication;
    private String googleDocLink;
    private LocalDateTime uploadAt;
    private Boolean isVerified;
    private UserDto supervisor;
    private UserDto coSupervisor;
    private List<ContributorResponseDto> contributors;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String courseName;
    private String degreeName;
    private String categoryName;

    @Data
    public static class ContributorResponseDto {
        private String enrollmentNumber;
        private String name;
        private String email;
        private String role;
    }

    @Data
    public static class UserDto {
        private String enrollmentNumber;
        private String name;
        private String email;
        private String role;
    }

    public ResourceResponseDto() {
        this.timestamp = LocalDateTime.now();
        this.status = "success";
    }
}
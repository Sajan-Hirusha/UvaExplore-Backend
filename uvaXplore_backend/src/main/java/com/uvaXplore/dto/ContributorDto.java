package com.AcademicStructures.dto;

import lombok.Data;

@Data
public class ContributorDto {
    private String enrollmentNumber;
    private String name;
    private String email;
    private String role; // "author", "editor", etc.
}
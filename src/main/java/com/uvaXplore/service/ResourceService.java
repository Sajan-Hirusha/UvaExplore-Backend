package com.uvaXplore.service;

import com.uvaXplore.dto.ContributorDto;
import com.uvaXplore.dto.ResourceCreateDto;
import com.uvaXplore.dto.ResourceResponseDto;
import com.uvaXplore.dto.TextDto;
import com.uvaXplore.entity.Resource;
import com.uvaXplore.entity.ResourceContributor;
import com.uvaXplore.entity.ResourceImage;
import com.uvaXplore.entity.User;
import com.uvaXplore.repo.ResourceRepository;
import com.uvaXplore.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@NoArgsConstructor
@AllArgsConstructor
@Service
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    private FlaskService flaskService;

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UserRepository userRepository;

    // Step 1: Extract the first two lines and all text
    public ResponseEntity<TextDto> extractTextFromPdf(MultipartFile file) {
        try {
            Map<String, Object> flaskResponse = flaskService.processPdf(file);

            TextDto textDto = new TextDto();
            textDto.setAll_text((String) flaskResponse.get("all_text"));
            textDto.setFirst_two_text((String) flaskResponse.get("first_two_text"));
            textDto.setSummarize_text("");
            textDto.setRequest_id((String) flaskResponse.get("request_id"));
            textDto.setStatus("processing");

            return ResponseEntity.ok(textDto);

        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    // Step 2: Fetch the summarized text once it is ready
    public ResponseEntity<TextDto> fetchSummarizedText(String requestId) {
        try {
            String summarizeText = flaskService.getSummary(requestId);
            TextDto textDto = new TextDto();
            textDto.setStatus(summarizeText != null ? "complete" : "processing");
            textDto.setSummarize_text(summarizeText);
            return ResponseEntity.ok(textDto);

        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @Transactional
    public ResourceResponseDto createResource(ResourceCreateDto dto) {
        Resource resource = new Resource();

        // Set basic fields
        resource.setTitle(dto.getTitle());
        resource.setCourseId(dto.getCourseId());
        resource.setType(dto.getType());
        resource.setCategoryId(dto.getCategoryId());
        resource.setDegree(dto.getDegree());
        resource.setAbstractText(dto.getAbstractText());
        resource.setPublication(dto.getPublication());
        resource.setGoogleDocLink(dto.getGoogleDocLink());
        resource.setUploadAt(LocalDateTime.now());
        resource.setIsVerified(false);

        // Set document URL from Firebase
        resource.setDocumentPath(dto.getDocumentUrl());

        // Handle images from Firebase URLs
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            List<ResourceImage> images = dto.getImageUrls().stream()
                    .map(url -> {
                        ResourceImage image = new ResourceImage();
                        image.setUrl(url);
                        image.setResource(resource);
                        return image;
                    })
                    .collect(Collectors.toList());
            resource.setImages(images);
        }

        // Handle contributors
        if (dto.getContributors() != null) {
            List<ResourceContributor> contributors = dto.getContributors().stream()
                    .map(contributorDto -> {
                        User user = userRepository.findById(contributorDto.getEnrollmentNumber())
                                .orElseGet(() -> {
                                    // Create new user if not exists (optional)
                                    User newUser = new User();
                                    newUser.setEnrollmentNumber(contributorDto.getEnrollmentNumber());
                                    newUser.setName(contributorDto.getName());
                                    newUser.setEmail(contributorDto.getEmail());
                                    newUser.setRole("STUDENT");
                                    return userRepository.save(newUser);
                                });

                        ResourceContributor contributor = new ResourceContributor();
                        contributor.setResource(resource);
                        contributor.setUser(user);
                        contributor.setRole("AUTHOR"); // Default role or from DTO
                        return contributor;
                    })
                    .collect(Collectors.toList());
            resource.setContributors(contributors);
        }

        // Handle supervisor
        if (dto.getSupervisorEnrollment() != null && !dto.getSupervisorEnrollment().isEmpty()) {
            User supervisor = userRepository.findById(dto.getSupervisorEnrollment())
                    .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));
            validateLecturerRole(supervisor);
            resource.setSupervisor(supervisor);
        }

        // Handle co-supervisor
        if (dto.getCoSupervisorEnrollment() != null && !dto.getCoSupervisorEnrollment().isEmpty()) {
            User coSupervisor = userRepository.findById(dto.getCoSupervisorEnrollment())
                    .orElseThrow(() -> new IllegalArgumentException("Co-supervisor not found"));
            validateLecturerRole(coSupervisor);
            resource.setCoSupervisor(coSupervisor);
        }

        Resource savedResource = resourceRepository.save(resource);
        return mapToResponseDto(savedResource);
    }

    private void validateLecturerRole(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!"LECTURER".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException(
                    "User with enrollment " + user.getEnrollmentNumber() +
                            " must have LECTURER role. Current role: " + user.getRole()
            );
        }
    }
}

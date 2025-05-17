package com.uvaXplore.service;

import com.uvaXplore.dto.ResourceCreateDto;
import com.uvaXplore.dto.ResourceResponseDto;
import com.uvaXplore.dto.TextDto;
import com.uvaXplore.entity.*;
import com.uvaXplore.exception.BusinessRuleException;
import com.uvaXplore.repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private final CourseRepository courseRepository;
    @Autowired
    private final DegreeRepository degreeRepository;
    @Autowired
    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

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
        // Validate input
        if (dto == null) {
            throw new IllegalArgumentException("Resource data cannot be null");
        }

        logger.info("Creating resource: {}", dto);

        // Create new resource and manually set all fields
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setType(dto.getType());
        resource.setAbstractText(dto.getAbstractText());
        resource.setPublication(dto.getPublication());
        resource.setGoogleDocLink(dto.getGoogleDocLink());
        resource.setDocumentPath(dto.getDocumentPath());
        resource.setUploadAt(LocalDateTime.now());
        resource.setIsVerified(false);
        resource.setGithubLink(dto.getGithubLink());
        logger.info("Resource created: {}", dto.getAbstractText());
        // Validate and set relationships

        logger.info("Generating embedding from abstract text...");

        try {
            float[] embeddingArray = flaskService.generateEmbedding(dto.getAbstractText());
            resource.setEmbedding(embeddingArray);
        } catch (IOException e) {
            logger.error("Failed to generate embedding: {}", e.getMessage(), e);
            throw new RuntimeException("Embedding generation failed", e);
        }

        validateAndSetRelationships(dto, resource);

        // Save resource
        Resource savedResource = resourceRepository.save(resource);

        // Map to response DTO
        return mapToResponseDto(savedResource);
    }

    private void validateAndSetRelationships(ResourceCreateDto dto, Resource resource) {
        // Validate and set course
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + dto.getCourseId()));
            resource.setCourse(course);
        }

        // Validate and set degree
        if (dto.getDegreeId() != null) {
            Degree degree = degreeRepository.findById(dto.getDegreeId())
                    .orElseThrow(() -> new EntityNotFoundException("Degree not found with id: " + dto.getDegreeId()));
            resource.setDegree(degree);
        }

        // Set category if provided
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId()));
            resource.setCategory(category);
        }

        // Process images
        if (dto.getImageUrls() != null) {
            resource.setImages(dto.getImageUrls().stream()
                    .map(url -> {
                        ResourceImage image = new ResourceImage();
                        image.setUrl(url);
                        image.setResource(resource);
                        return image;
                    })
                    .collect(Collectors.toList()));
        }

        // Process contributors
        if (dto.getContributors() != null) {
            resource.setContributors(dto.getContributors().stream()
                    .map(contributorDto -> {
                        User user = userRepository.findById(contributorDto.getEnrollmentNumber())
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "User not found with enrollment: " + contributorDto.getEnrollmentNumber()));

                        ResourceContributor contributor = new ResourceContributor();
                        contributor.setResource(resource);
                        contributor.setUser(user);
                        contributor.setRole(contributorDto.getRole());
                        return contributor;
                    })
                    .collect(Collectors.toList()));
        }

        // Process supervisor
        if (StringUtils.hasText(dto.getSupervisorEnrollment())) {
            User supervisor = userRepository.findById(dto.getSupervisorEnrollment())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Supervisor not found with enrollment: " + dto.getSupervisorEnrollment()));
            validateLecturerRole(supervisor);
            resource.setSupervisor(supervisor);
        }

        // Process co-supervisor
        if (StringUtils.hasText(dto.getCoSupervisorEnrollment())) {
            User coSupervisor = userRepository.findById(dto.getCoSupervisorEnrollment())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Co-supervisor not found with enrollment: " + dto.getCoSupervisorEnrollment()));
            validateLecturerRole(coSupervisor);
            resource.setCoSupervisor(coSupervisor);
        }
    }

    private ResourceResponseDto mapToResponseDto(Resource resource) {
        ResourceResponseDto response = new ResourceResponseDto();

        // Map basic fields
        response.setResourceId(resource.getResourceId());
        response.setTitle(resource.getTitle());
        response.setType(resource.getType());
        response.setAbstractText(resource.getAbstractText());
        response.setPublication(resource.getPublication());
        response.setGoogleDocLink(resource.getGoogleDocLink());
        response.setDocumentUrl(resource.getDocumentPath());
        response.setUploadAt(resource.getUploadAt());
        response.setIsVerified(resource.getIsVerified());

        // Map relationships
        if (resource.getCourse() != null) {
            response.setCourseName(resource.getCourse().getName());
        }
        if (resource.getDegree() != null) {
            response.setDegreeName(resource.getDegree().getName());
        }
        if (resource.getCategory() != null) {
            response.setCategoryName(resource.getCategory().getName());
        }

        // Map images
        if (resource.getImages() != null) {
            response.setImageUrls(resource.getImages().stream()
                    .map(ResourceImage::getUrl)
                    .collect(Collectors.toList()));
        }

        // Map contributors
        if (resource.getContributors() != null) {
            response.setContributors(resource.getContributors().stream()
                    .map(contributor -> {
                        ResourceResponseDto.ContributorResponseDto dto = new ResourceResponseDto.ContributorResponseDto();
                        dto.setEnrollmentNumber(contributor.getUser().getEnrollmentNumber());
                        dto.setName(contributor.getUser().getName());
                        dto.setRole(contributor.getRole());
                        return dto;
                    })
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private void validateLecturerRole(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!"LECTURER".equalsIgnoreCase(user.getRole())) {
            throw new BusinessRuleException(
                    "User with enrollment " + user.getEnrollmentNumber() +
                            " must have LECTURER role. Current role: " + user.getRole());
        }
    }
}
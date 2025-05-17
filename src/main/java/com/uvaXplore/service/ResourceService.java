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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
        // Map from DTO to Entity
        Resource resource = modelMapper.map(dto, Resource.class);
        resource.setUploadAt(LocalDateTime.now());
        resource.setIsVerified(false);

        // Process relationships that need special handling
        processRelationships(dto, resource);

        // Save resource
        Resource savedResource = resourceRepository.save(resource);

        // Map to response DTO
        return mapToResponseDto(savedResource);
    }

    private void processRelationships(ResourceCreateDto dto, Resource resource) {
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
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Contributor not found: " + contributorDto.getEnrollmentNumber()));

                        ResourceContributor contributor = new ResourceContributor();
                        contributor.setResource(resource);
                        contributor.setUser(user);
                        contributor.setRole(contributorDto.getRole());
                        return contributor;
                    })
                    .collect(Collectors.toList()));
        }

        // Process supervisor
        if (dto.getSupervisorEnrollment() != null && !dto.getSupervisorEnrollment().isEmpty()) {
            User supervisor = userRepository.findById(dto.getSupervisorEnrollment())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Supervisor not found: " + dto.getSupervisorEnrollment()));
            validateLecturerRole(supervisor);
            resource.setSupervisor(supervisor);
        }

        // Process co-supervisor
        if (dto.getCoSupervisorEnrollment() != null && !dto.getCoSupervisorEnrollment().isEmpty()) {
            User coSupervisor = userRepository.findById(dto.getCoSupervisorEnrollment())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Co-supervisor not found: " + dto.getCoSupervisorEnrollment()));
            validateLecturerRole(coSupervisor);
            resource.setCoSupervisor(coSupervisor);
        }


    }

    private ResourceResponseDto mapToResponseDto(Resource resource) {
        // Configure custom mappings if needed
        modelMapper.typeMap(Resource.class, ResourceResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getDocumentPath(), ResourceResponseDto::setDocumentUrl);
                    mapper.map(src -> src.getSupervisor(), ResourceResponseDto::setSupervisor);
                    mapper.map(src -> src.getCoSupervisor(), ResourceResponseDto::setCoSupervisor);
                });

        // Perform the mapping
        ResourceResponseDto response = modelMapper.map(resource, ResourceResponseDto.class);

        // Map collections separately if needed
        if (resource.getImages() != null) {
            response.setImageUrls(resource.getImages().stream()
                    .map(ResourceImage::getUrl)
                    .collect(Collectors.toList()));
        }

        return response;
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

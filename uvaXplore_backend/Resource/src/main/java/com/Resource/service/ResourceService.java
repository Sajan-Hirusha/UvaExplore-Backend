package com.Resource.service;

import com.Resource.dto.ResourceCreateDto;
import com.Resource.dto.TextDto;
import com.Resource.entity.Resource;
import com.Resource.entity.ResourceContributor;
import com.Resource.entity.ResourceImage;
import com.Resource.repo.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

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
    public ResponseEntity<String> createResource(ResourceCreateDto dto) {
        if (dto == null) {
            return ResponseEntity.badRequest().body("Resource data cannot be null.");
        }

        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setCourseId(dto.getCourseId());
        resource.setDegreeId(dto.getDegreeId());
        resource.setCategoryId(dto.getCategoryId());
        resource.setType(dto.getType());
        resource.setAbstractText(dto.getAbstractText());
        resource.setDocumentPath(dto.getDocumentPath());
        resource.setIsVerified(false);
        resource.setUploadAt(LocalDateTime.now());
        resource.setPublication(dto.getPublication());
        resource.setGoogleDocLink(dto.getGoogleDocLink());
        resource.setSourceCodePath(dto.getSourceCodePath());
        resource.setSupervisorEnrollment(dto.getSupervisorEnrollment());
        resource.setCoSupervisorEnrollment(dto.getCoSupervisorEnrollment());

        try {
            float[] embeddingArray = flaskService.generateEmbedding(dto.getAbstractText());
            resource.setEmbedding(embeddingArray);
        } catch (IOException e) {
            logger.error("Failed to generate embedding: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Embedding generation failed.");
        }

        try {
            validateAndSetRelationships(dto, resource);
            Resource savedResource = resourceRepository.save(resource);

            if (savedResource != null && savedResource.getResourceId() != null) {
                return ResponseEntity.ok("Resource saved successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save resource.");
            }

        } catch (Exception e) {
            logger.error("Failed to save resource: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving the resource.");
        }
    }


    private void validateAndSetRelationships(ResourceCreateDto dto, Resource resource) {
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
                        ResourceContributor contributor = new ResourceContributor();
                        contributor.setResource(resource);
                        contributor.setUserEnrollmentNumber(contributorDto.getEnrollmentNumber());
                        return contributor;
                    })
                    .collect(Collectors.toList()));
        }
    }

//    private ResourceResponseDto mapToResponseDto(Resource resource) {
//        ResourceResponseDto response = new ResourceResponseDto();
//
//        // Map basic fields
//        response.setResourceId(resource.getResourceId());
//        response.setTitle(resource.getTitle());
//        response.setType(resource.getType());
//        response.setAbstractText(resource.getAbstractText());
//        response.setPublication(resource.getPublication());
//        response.setGoogleDocLink(resource.getGoogleDocLink());
//        response.setDocumentUrl(resource.getDocumentPath());
//        response.setUploadAt(resource.getUploadAt());
//        response.setIsVerified(resource.getIsVerified());
//        response.setCourseId(resource.getCourseId());
//        response.setDegreeId(resource.getDegreeId());
////response.setCategoryId(resource.getCategoryId());
//
//
//        // Map images
//        if (resource.getImages() != null) {
//            response.setImageUrls(resource.getImages().stream()
//                    .map(ResourceImage::getUrl)
//                    .collect(Collectors.toList()));
//        }
//
//        // Map contributors
//        if (resource.getContributors() != null) {
//            response.setContributors(resource.getContributors().stream()
//                    .map(contributor -> {
//                        ResourceResponseDto.ContributorResponseDto dto = new ResourceResponseDto.ContributorResponseDto();
//                        dto.setEnrollmentNumber(contributor.getEnrollmentNumber());
//                        dto.setName(contributor.getUser().getName());
//                        return dto;
//                    })
//                    .collect(Collectors.toList()));
//        }
//
//        return response;
//    }
}
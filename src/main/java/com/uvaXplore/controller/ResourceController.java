package com.uvaXplore.controller;

import com.uvaXplore.dto.ResourceCreateDto;
import com.uvaXplore.dto.ResourceResponseDto;
import com.uvaXplore.dto.TextDto;
import com.uvaXplore.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/createResource")
    public ResponseEntity<ResourceResponseDto> createResource(@RequestBody ResourceCreateDto dto) {
        logger.info("Create resource");
        return ResponseEntity.ok(resourceService.createResource(dto));
    }

    @PostMapping("/extractText")
    public ResponseEntity<TextDto> uploadPdf(
            @RequestParam("file") MultipartFile file) {
        return resourceService.extractTextFromPdf(file);
    }

        @GetMapping("/fetchSummary/{requestId}")
    public ResponseEntity<TextDto> fetchSummarizedText(@PathVariable("requestId") String requestId) {
        try {
            return resourceService.fetchSummarizedText(requestId);

        } catch (Exception e) {
            logger.error("Error during fetching summary: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

}
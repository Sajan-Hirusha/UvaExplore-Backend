package com.uvaXplore.controller;


import com.uvaXplore.dto.TextDto;
import com.uvaXplore.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "http://localhost:5173")
public class ResourceController {

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }


    @PostMapping("/extractText")
    public ResponseEntity<TextDto> uploadPdf(
            @RequestParam("file") MultipartFile file) {
        return resourceService.extractTextFromPdf(file);
    }

}
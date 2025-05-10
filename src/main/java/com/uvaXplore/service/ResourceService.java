package com.uvaXplore.service;

import com.uvaXplore.dto.TextDto;
import com.uvaXplore.repo.ResourceRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Service
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    private FlaskService flaskService;

    @Autowired
    private ResourceRepository resourceRepository;

    public ResponseEntity<TextDto> extractTextFromPdf(MultipartFile file) {
        try {
            Map<String, Object> flaskResponse = flaskService.processPdf(file);

            TextDto textDto = new TextDto();
            textDto.setAll_text((String) flaskResponse.get("all_text"));
            textDto.setFirst_two_text((String) flaskResponse.get("first_two_text"));

            return ResponseEntity.ok(textDto);

        } catch (Exception e) {
            logger.error("An error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}

package com.uvaXplore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FlaskService {

    @Value("${flask.service.url}")
    private String flaskUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> processPdf(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = new RestTemplate().postForEntity(
                flaskUrl + "/api/v1/processStart",
                requestEntity,
                Map.class
        );

        return response.getBody();
    }

    public String getSummary(String requestId) {
        ResponseEntity<Map> response = new RestTemplate().getForEntity(
                flaskUrl + "/api/v1/getSummary/" + requestId,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        return (String) responseBody.get("summarize_text");
    }

    public float[] generateEmbedding(String text) throws IOException {
        // 1. Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Prepare JSON body with text
        Map<String, String> body = Collections.singletonMap("text", text);

        // 3. Send POST request to Flask
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                flaskUrl + "/api/v1/embed",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // 4. Process the response
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("embedding")) {
            throw new IOException("Invalid response from embedding service");
        }

        // 5. Convert the embedding to float array
        Object embeddingObj = responseBody.get("embedding");
        if (!(embeddingObj instanceof List)) {
            throw new IOException("Unexpected embedding format");
        }

        List<?> embeddingList = (List<?>) embeddingObj;
        float[] embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            embedding[i] = ((Number) embeddingList.get(i)).floatValue();
        }

        return embedding;
    }
}

//package com.uvaXplore.controller;
//
//import com.uvaXplore.entity.Resource;
//import com.uvaXplore.repo.ResourceRepository;
//import com.uvaXplore.service.FlaskService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/projects")
//@CrossOrigin(origins = "http://localhost:5173")
//public class ResourceController {
//
//    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
//
//    @Autowired
//    private FlaskService flaskService;
//
//    @Autowired
//    private ResourceRepository projectRepository;
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadPdf(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("title") String title) {
//
//        try {
//            // 1. Call Flask microservice
//            Map<String, Object> flaskResponse = flaskService.processPdf(file);
//
//            // 2. Save to database
//            Resource project = new Resource();
//            project.setTitle(title);
//            List<Double> embeddingList = (List<Double>) flaskResponse.get("embedding");
//            float[] embeddingArray = new float[embeddingList.size()];
//            for (int i = 0; i < embeddingList.size(); i++) {
//                embeddingArray[i] = embeddingList.get(i).floatValue();
//            }
//            project.setEmbedding(embeddingArray);
//
//
//            projectRepository.save(project);
//
//            return ResponseEntity.ok("PDF processed successfully");
//
//        } catch (Exception e) {
//            logger.error("An error occurred: {}", e.getMessage(), e);
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<Resource>> searchResources(
//            @RequestParam String query) {
//
//        List<Resource> projects = projectRepository.findByDescriptionContaining(query);
//        return ResponseEntity.ok(projects);
//    }
//}
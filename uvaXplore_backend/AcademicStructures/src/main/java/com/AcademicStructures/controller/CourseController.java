package com.AcademicStructures.controller;

import com.AcademicStructures.entity.Course;
import com.AcademicStructures.service.CourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    @Autowired
    private CourceService courceService;

    @GetMapping("/getOne/{id}")
    public Optional<Course> getCourseById(@PathVariable String id) {
        return courceService.getCourseById(id);
    }

}

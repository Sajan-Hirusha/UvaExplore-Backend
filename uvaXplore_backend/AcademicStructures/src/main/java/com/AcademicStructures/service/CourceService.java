package com.AcademicStructures.service

import com.AcademicStructures.entity.Course;
import com.AcademicStructures.repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.Optional;

@Service
public class CourceService {

    @Autowired
    private CourseRepository courseRepository;

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }
}
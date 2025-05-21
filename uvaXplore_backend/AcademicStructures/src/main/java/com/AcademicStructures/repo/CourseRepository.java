package com.AcademicStructures.repo;

import com.AcademicStructures.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {
}

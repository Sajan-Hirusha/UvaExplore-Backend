package com.AcademicStructures.repo;

import com.AcademicStructures.entity.Degree;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DegreeRepository extends JpaRepository<Degree, String> {
}

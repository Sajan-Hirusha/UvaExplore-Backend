package com.uvaXplore.repo;

import com.uvaXplore.entity.Degree;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DegreeRepository extends JpaRepository<Degree, String> {
}

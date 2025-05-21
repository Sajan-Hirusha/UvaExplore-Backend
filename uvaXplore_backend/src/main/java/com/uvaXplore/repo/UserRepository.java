package com.AcademicStructures.repo;

import com.AcademicStructures.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User , String> {
}

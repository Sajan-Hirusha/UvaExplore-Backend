package com.uvaXplore.repo;

import com.uvaXplore.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

//    List<Resource> findByDescriptionContaining(String keyword);

//    @Query(value = """
//        SELECT id, title, description,
//               embedding <=> CAST(:embedding AS vector) AS similarity
//        FROM projects
//        ORDER BY similarity
//        LIMIT 10
//        """, nativeQuery = true)
//    List<Object[]> findSimilarResources(@Param("embedding") float[] embedding);
}

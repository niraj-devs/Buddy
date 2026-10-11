package com.buddy.development.repository;

import com.buddy.development.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    List<Roadmap> findByCareerPathId(Long careerPathId);

    Optional<Roadmap> findByCareerPathIdAndVersion(
        Long careerPathId,
        Integer version
    );
}

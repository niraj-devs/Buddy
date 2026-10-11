package com.buddy.development.repository;

import com.buddy.development.entity.RoadmapSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapSkillRepository extends JpaRepository<RoadmapSkill, Long> {

    List<RoadmapSkill> findByRoadmapIdOrderBySequenceOrder(Long roadmapId);

    Optional<RoadmapSkill> findByRoadmapIdAndSkillId(
        Long roadmapId,
        Long skillId
    );
}

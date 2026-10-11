package com.buddy.planning.repository;

import com.buddy.planning.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByGoalIdOrderBySequenceNumber(Long goalId);

    List<Milestone> findByGoalIdAndStatus(
        Long goalId,
        String status
    );
}

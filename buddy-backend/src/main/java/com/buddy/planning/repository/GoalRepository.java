package com.buddy.planning.repository;

import com.buddy.planning.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByStudentId(Long studentId);

    List<Goal> findByStudentIdAndStatus(
        Long studentId,
        String status
    );

    List<Goal> findByStudentIdAndGoalType(
        Long studentId,
        String goalType
    );
}

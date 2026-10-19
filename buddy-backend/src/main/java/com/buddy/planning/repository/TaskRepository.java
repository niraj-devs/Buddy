package com.buddy.planning.repository;

import com.buddy.planning.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStudentId(Long studentId);

    List<Task> findByMilestoneId(Long milestoneId);

    List<Task> findByStudentIdAndStatus(
        Long studentId,
        String status
    );

    List<Task> findByStudentIdAndDueDateBefore(
        Long studentId,
        LocalDate date
    );

    java.util.Optional<Task> findByIdAndStudentId(
        Long taskId,
        Long studentId
    );
}
package com.buddy.academic.repository;

import com.buddy.academic.entity.StudentTopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentTopicProgressRepository
        extends JpaRepository<StudentTopicProgress, Long> {

    Optional<StudentTopicProgress> findByStudentIdAndTopicId(
        Long studentId,
        Long topicId
    );

    List<StudentTopicProgress> findByStudentId(Long studentId);

    List<StudentTopicProgress> findByStudentIdAndStatus(
        Long studentId,
        String status
    );
}

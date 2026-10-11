package com.buddy.academic.repository;

import com.buddy.academic.entity.StudentQuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentQuestionAttemptRepository
        extends JpaRepository<StudentQuestionAttempt, Long> {

    List<StudentQuestionAttempt> findByStudentId(Long studentId);

    List<StudentQuestionAttempt> findByStudentIdAndQuestionId(
        Long studentId,
        Long questionId
    );
}

package com.buddy.academic.repository;

import com.buddy.academic.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findBySemesterId(Long semesterId);

    Optional<Subject> findBySemesterIdAndCode(
        Long semesterId,
        String code
    );
}

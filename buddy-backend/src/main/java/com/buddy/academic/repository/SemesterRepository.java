package com.buddy.academic.repository;

import com.buddy.academic.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

    List<Semester> findByBranchIdOrderBySemesterNumber(Long branchId);

    Optional<Semester> findByBranchIdAndSemesterNumber(
        Long branchId,
        Short semesterNumber
    );
}

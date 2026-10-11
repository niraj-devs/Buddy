package com.buddy.student.repository;

import com.buddy.student.entity.RepresentativeAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepresentativeAssignmentRepository
        extends JpaRepository<RepresentativeAssignment, Long> {

    List<RepresentativeAssignment> findByStudentId(Long studentId);

    List<RepresentativeAssignment> findBySectionId(Long sectionId);

    List<RepresentativeAssignment> findByStudentIdAndActive(
        Long studentId,
        boolean active
    );
}

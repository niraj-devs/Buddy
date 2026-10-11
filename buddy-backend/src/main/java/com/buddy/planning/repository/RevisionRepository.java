package com.buddy.planning.repository;

import com.buddy.planning.entity.Revision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Long> {

    List<Revision> findByStudentId(Long studentId);

    List<Revision> findByStudentIdAndStatus(
        Long studentId,
        String status
    );

    List<Revision> findByStudentIdAndScheduledDate(
        Long studentId,
        LocalDate scheduledDate
    );
}

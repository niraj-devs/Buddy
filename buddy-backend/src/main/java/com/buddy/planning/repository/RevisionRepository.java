package com.buddy.planning.repository;

import com.buddy.planning.entity.Revision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    List<Revision> findByStudentIdAndStatusAndScheduledDateGreaterThanEqualOrderByScheduledDateAsc(
            Long studentId,
            String status,
            LocalDate scheduledDate
    );

    Optional<Revision> findTopByProgressIdOrderByRevisionNumberDesc(
            Long progressId
    );

    boolean existsByProgressIdAndRevisionNumber(
            Long progressId,
            Integer revisionNumber
    );
}

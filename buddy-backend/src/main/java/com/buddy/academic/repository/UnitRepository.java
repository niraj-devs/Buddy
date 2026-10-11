package com.buddy.academic.repository;

import com.buddy.academic.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {

    List<Unit> findBySubjectIdOrderByUnitNumber(Long subjectId);

    Optional<Unit> findBySubjectIdAndUnitNumber(
        Long subjectId,
        Short unitNumber
    );
}

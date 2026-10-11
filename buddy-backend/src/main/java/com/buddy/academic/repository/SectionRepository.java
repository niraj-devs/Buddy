package com.buddy.academic.repository;

import com.buddy.academic.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findBySemesterId(Long semesterId);

    Optional<Section> findBySemesterIdAndName(
        Long semesterId,
        String name
    );
}

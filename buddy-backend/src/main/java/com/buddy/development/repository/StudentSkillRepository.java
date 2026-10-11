package com.buddy.development.repository;

import com.buddy.development.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {

    Optional<StudentSkill> findByStudentIdAndSkillId(
        Long studentId,
        Long skillId
    );

    List<StudentSkill> findByStudentId(Long studentId);

    List<StudentSkill> findByStudentIdAndStatus(
        Long studentId,
        String status
    );
}

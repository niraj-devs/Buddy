package com.buddy.development.repository;

import com.buddy.development.entity.SkillEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillEvidenceRepository
        extends JpaRepository<SkillEvidence, Long> {

    List<SkillEvidence> findByStudentId(Long studentId);

    List<SkillEvidence> findByStudentIdAndSkillId(
        Long studentId,
        Long skillId
    );

    List<SkillEvidence> findByStudentIdAndVerificationStatus(
        Long studentId,
        String verificationStatus
    );
}

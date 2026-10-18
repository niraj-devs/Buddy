package com.buddy.development.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SkillEvidenceResponse(
        Long id,
        Long skillId,
        String skillName,
        String evidenceType,
        String title,
        String description,
        String referenceUrl,
        LocalDate evidenceDate,
        String verificationStatus,
        LocalDateTime createdAt
) {
}
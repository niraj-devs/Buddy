package com.buddy.development.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StudentSkillResponse(
        Long skillId,
        String skillName,
        String category,
        String status,
        BigDecimal progressPercentage,
        String proficiencyLevel,
        LocalDateTime startedAt,
        LocalDateTime achievedAt
) {
}

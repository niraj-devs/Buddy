package com.buddy.development.dto;

import java.math.BigDecimal;
import java.util.List;

public record RoadmapSkillResponse(
        Long skillId,
        String name,
        String category,
        String description,
        Integer sequenceOrder,
        boolean core,
        boolean unlocked,
        List<String> unmetPrerequisites,
        String status,
        BigDecimal progressPercentage
) {
}

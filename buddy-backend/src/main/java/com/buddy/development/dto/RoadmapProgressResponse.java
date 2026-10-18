package com.buddy.development.dto;

import java.math.BigDecimal;
import java.util.List;

public record RoadmapProgressResponse(
        Long roadmapId,
        String roadmapName,
        int totalSkills,
        int completedSkills,
        BigDecimal progressPercentage,
        List<StudentSkillResponse> skills
) {
}

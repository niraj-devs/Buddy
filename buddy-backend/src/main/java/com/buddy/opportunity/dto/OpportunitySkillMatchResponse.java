package com.buddy.opportunity.dto;

import java.math.BigDecimal;
import java.util.List;

public record OpportunitySkillMatchResponse(
        Long opportunityId,
        int totalRequiredSkills,
        int matchedSkillCount,
        List<String> matchedSkills,
        List<String> missingSkills,
        BigDecimal matchPercentage
) {
}
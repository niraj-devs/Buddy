package com.buddy.opportunity.dto;

import java.math.BigDecimal;
import java.util.List;

public record OpportunityRecommendationResponse(
        OpportunityResponse opportunity,
        boolean eligible,
        List<String> eligibilityReasons,
        int totalRequiredSkills,
        int matchedSkillCount,
        List<String> matchedSkills,
        List<String> missingSkills,
        BigDecimal skillMatchPercentage,
        BigDecimal recommendationScore,
        String recommendationStatus,
        List<String> recommendationReasons
) {
}
package com.buddy.opportunity.dto;

public record OpportunitySkillResponse(
        Long skillId,
        String skillName,
        String category,
        boolean required
) {
}
package com.buddy.opportunity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OpportunityResponse(
        Long id,
        String title,
        String organization,
        String description,
        String opportunityType,
        String location,
        String workMode,
        BigDecimal minCgpa,
        Short targetGradYear,
        String applicationUrl,
        LocalDateTime deadline,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

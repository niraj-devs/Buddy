package com.buddy.opportunity.dto;

import java.util.List;

public record OpportunityEligibilityResponse(
        Long opportunityId,
        boolean eligible,
        List<String> reasons
) {
}
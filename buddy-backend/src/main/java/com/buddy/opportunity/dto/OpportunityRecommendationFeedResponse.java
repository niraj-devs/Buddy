package com.buddy.opportunity.dto;

import java.util.List;

public record OpportunityRecommendationFeedResponse(
        List<OpportunityRecommendationResponse> recommendations,
        List<OpportunityRecommendationResponse> alternatives
) {
}
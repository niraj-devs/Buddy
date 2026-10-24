package com.buddy.opportunity.controller;

import com.buddy.opportunity.dto.OpportunityEligibilityResponse;
import com.buddy.opportunity.dto.OpportunityRecommendationFeedResponse;
import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.dto.OpportunitySkillMatchResponse;
import com.buddy.opportunity.service.OpportunityEligibilityService;
import com.buddy.opportunity.service.OpportunityRecommendationService;
import com.buddy.opportunity.service.OpportunityService;
import com.buddy.opportunity.service.OpportunitySkillMatchService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final OpportunityEligibilityService eligibilityService;
    private final OpportunitySkillMatchService skillMatchService;
    private final OpportunityRecommendationService recommendationService;

    public OpportunityController(
            OpportunityService opportunityService,
            OpportunityEligibilityService eligibilityService,
            OpportunitySkillMatchService skillMatchService,
            OpportunityRecommendationService recommendationService
    ) {
        this.opportunityService = opportunityService;
        this.eligibilityService = eligibilityService;
        this.skillMatchService = skillMatchService;
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<OpportunityResponse> getOpportunities(
            @RequestParam(required = false) String type
    ) {
        if (type == null || type.isBlank()) {
            return opportunityService.getActiveOpportunities();
        }

        return opportunityService.getActiveOpportunitiesByType(type);
    }

    @GetMapping("/recommendations")
    public OpportunityRecommendationFeedResponse getRecommendations(
            Authentication authentication
    ) {
        return recommendationService.getRecommendations(
                authentication.getName()
        );
    }

    @GetMapping("/{id}")
    public OpportunityResponse getOpportunity(
            @PathVariable Long id
    ) {
        return opportunityService.getActiveOpportunity(id);
    }

    @GetMapping("/{id}/eligibility")
    public OpportunityEligibilityResponse checkEligibility(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return eligibilityService.checkEligibility(
                authentication.getName(),
                id
        );
    }

    @GetMapping("/{id}/skill-match")
    public OpportunitySkillMatchResponse matchSkills(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return skillMatchService.matchSkills(
                authentication.getName(),
                id
        );
    }
}

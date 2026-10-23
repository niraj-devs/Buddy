package com.buddy.opportunity.controller;

import com.buddy.opportunity.dto.OpportunityEligibilityResponse;
import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.service.OpportunityEligibilityService;
import com.buddy.opportunity.service.OpportunityService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final OpportunityEligibilityService eligibilityService;

    public OpportunityController(
            OpportunityService opportunityService,
            OpportunityEligibilityService eligibilityService
    ) {
        this.opportunityService = opportunityService;
        this.eligibilityService = eligibilityService;
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
}
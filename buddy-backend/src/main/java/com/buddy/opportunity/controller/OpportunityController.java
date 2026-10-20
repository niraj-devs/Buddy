package com.buddy.opportunity.controller;

import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.service.OpportunityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(
            OpportunityService opportunityService
    ) {
        this.opportunityService = opportunityService;
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
}

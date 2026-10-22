package com.buddy.opportunity.controller;

import com.buddy.opportunity.dto.OpportunityCreateRequest;
import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.dto.OpportunitySkillAssignmentRequest;
import com.buddy.opportunity.dto.OpportunitySkillResponse;
import com.buddy.opportunity.service.AdminOpportunityService;
import com.buddy.opportunity.service.OpportunitySkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/opportunities")
public class AdminOpportunityController {

    private final AdminOpportunityService adminOpportunityService;
    private final OpportunitySkillService opportunitySkillService;

    public AdminOpportunityController(
            AdminOpportunityService adminOpportunityService,
            OpportunitySkillService opportunitySkillService
    ) {
        this.adminOpportunityService = adminOpportunityService;
        this.opportunitySkillService = opportunitySkillService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpportunityResponse createOpportunity(
            @Valid @RequestBody OpportunityCreateRequest request
    ) {
        return adminOpportunityService.createOpportunity(request);
    }

    @PutMapping("/{opportunityId}/skills")
    public List<OpportunitySkillResponse> replaceOpportunitySkills(
            @PathVariable Long opportunityId,
            @Valid @RequestBody OpportunitySkillAssignmentRequest request
    ) {
        return opportunitySkillService.replaceSkills(opportunityId, request);
    }
}
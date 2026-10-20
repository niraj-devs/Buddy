package com.buddy.opportunity.controller;

import com.buddy.opportunity.dto.OpportunityCreateRequest;
import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.service.AdminOpportunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/opportunities")
public class AdminOpportunityController {

    private final AdminOpportunityService adminOpportunityService;

    public AdminOpportunityController(
            AdminOpportunityService adminOpportunityService
    ) {
        this.adminOpportunityService = adminOpportunityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpportunityResponse createOpportunity(
            @Valid @RequestBody OpportunityCreateRequest request
    ) {
        return adminOpportunityService.createOpportunity(request);
    }
}

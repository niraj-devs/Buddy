package com.buddy.opportunity.service;

import com.buddy.opportunity.dto.OpportunityCreateRequest;
import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.entity.Opportunity;
import com.buddy.opportunity.repository.OpportunityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class AdminOpportunityService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "INTERNSHIP",
            "JOB",
            "HACKATHON",
            "SCHOLARSHIP",
            "COMPETITION",
            "OTHER"
    );

    private static final Set<String> ALLOWED_WORK_MODES = Set.of(
            "ONSITE",
            "REMOTE",
            "HYBRID"
    );

    private final OpportunityRepository opportunityRepository;

    public AdminOpportunityService(
            OpportunityRepository opportunityRepository
    ) {
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional
    public OpportunityResponse createOpportunity(
            OpportunityCreateRequest request
    ) {
        String type = request.opportunityType().trim().toUpperCase();

        if (!ALLOWED_TYPES.contains(type)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid opportunity type"
            );
        }

        String workMode = null;

        if (request.workMode() != null && !request.workMode().isBlank()) {
            workMode = request.workMode().trim().toUpperCase();

            if (!ALLOWED_WORK_MODES.contains(workMode)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid work mode"
                );
            }
        }

        Opportunity opportunity = new Opportunity();
        opportunity.setTitle(request.title().trim());
        opportunity.setOrganization(request.organization().trim());
        opportunity.setDescription(request.description());
        opportunity.setOpportunityType(type);
        opportunity.setLocation(request.location());
        opportunity.setWorkMode(workMode);
        opportunity.setMinCgpa(request.minCgpa());
        opportunity.setTargetGradYear(request.targetGradYear());
        opportunity.setApplicationUrl(request.applicationUrl());
        opportunity.setDeadline(request.deadline());
        opportunity.setActive(true);

        Opportunity saved = opportunityRepository.save(opportunity);

        return new OpportunityResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getOrganization(),
                saved.getDescription(),
                saved.getOpportunityType(),
                saved.getLocation(),
                saved.getWorkMode(),
                saved.getMinCgpa(),
                saved.getTargetGradYear(),
                saved.getApplicationUrl(),
                saved.getDeadline(),
                saved.isActive(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}

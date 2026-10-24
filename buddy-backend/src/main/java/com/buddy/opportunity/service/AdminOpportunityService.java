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
        validateOpportunityRequest(request);

        Opportunity opportunity = new Opportunity();
        applyRequest(opportunity, request);
        opportunity.setActive(true);

        Opportunity saved = opportunityRepository.save(opportunity);
        return toResponse(saved);
    }

    @Transactional
    public OpportunityResponse updateOpportunity(
            Long opportunityId,
            OpportunityCreateRequest request
    ) {
        validateOpportunityRequest(request);

        Opportunity opportunity = opportunityRepository
                .findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Opportunity not found"
                ));

        applyRequest(opportunity, request);

        Opportunity saved = opportunityRepository.save(opportunity);
        return toResponse(saved);
    }

    private void validateOpportunityRequest(
            OpportunityCreateRequest request
    ) {
        String type = request.opportunityType().trim().toUpperCase();

        if (!ALLOWED_TYPES.contains(type)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid opportunity type"
            );
        }

        if (request.workMode() != null
                && !request.workMode().isBlank()) {
            String workMode = request.workMode().trim().toUpperCase();

            if (!ALLOWED_WORK_MODES.contains(workMode)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid work mode"
                );
            }
        }
    }

    private void applyRequest(
            Opportunity opportunity,
            OpportunityCreateRequest request
    ) {
        opportunity.setTitle(request.title().trim());
        opportunity.setOrganization(request.organization().trim());
        opportunity.setDescription(request.description());
        opportunity.setOpportunityType(
                request.opportunityType().trim().toUpperCase()
        );
        opportunity.setLocation(request.location());
        opportunity.setWorkMode(
                request.workMode() == null || request.workMode().isBlank()
                        ? null
                        : request.workMode().trim().toUpperCase()
        );
        opportunity.setMinCgpa(request.minCgpa());
        opportunity.setTargetGradYear(request.targetGradYear());
        opportunity.setApplicationUrl(request.applicationUrl());
        opportunity.setDeadline(request.deadline());
    }

    private OpportunityResponse toResponse(Opportunity opportunity) {
        return new OpportunityResponse(
                opportunity.getId(),
                opportunity.getTitle(),
                opportunity.getOrganization(),
                opportunity.getDescription(),
                opportunity.getOpportunityType(),
                opportunity.getLocation(),
                opportunity.getWorkMode(),
                opportunity.getMinCgpa(),
                opportunity.getTargetGradYear(),
                opportunity.getApplicationUrl(),
                opportunity.getDeadline(),
                opportunity.isActive(),
                opportunity.getCreatedAt(),
                opportunity.getUpdatedAt()
        );
    }
}

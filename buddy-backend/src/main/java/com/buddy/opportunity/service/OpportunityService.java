package com.buddy.opportunity.service;

import com.buddy.opportunity.dto.OpportunityResponse;
import com.buddy.opportunity.entity.Opportunity;
import com.buddy.opportunity.repository.OpportunityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;

    public OpportunityService(
            OpportunityRepository opportunityRepository
    ) {
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> getActiveOpportunities() {
        return opportunityRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OpportunityResponse> getActiveOpportunitiesByType(
            String opportunityType
    ) {
        String normalizedType = opportunityType.trim().toUpperCase();

        return opportunityRepository
                .findByOpportunityTypeAndActiveTrue(normalizedType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OpportunityResponse getActiveOpportunity(Long opportunityId) {
        Opportunity opportunity = opportunityRepository
                .findById(opportunityId)
                .filter(Opportunity::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Opportunity not found"
                ));

        return mapToResponse(opportunity);
    }

    private OpportunityResponse mapToResponse(Opportunity opportunity) {
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

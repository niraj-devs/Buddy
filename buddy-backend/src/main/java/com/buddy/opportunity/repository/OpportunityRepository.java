package com.buddy.opportunity.repository;

import com.buddy.opportunity.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpportunityRepository
        extends JpaRepository<Opportunity, Long> {

    List<Opportunity> findByActiveTrue();

    List<Opportunity> findByOpportunityTypeAndActiveTrue(
        String opportunityType
    );

    List<Opportunity> findByOrganizationContainingIgnoreCase(
        String organization
    );
}

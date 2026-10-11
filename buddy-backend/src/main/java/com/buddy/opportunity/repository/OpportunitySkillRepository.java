package com.buddy.opportunity.repository;

import com.buddy.opportunity.entity.OpportunitySkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpportunitySkillRepository
        extends JpaRepository<OpportunitySkill, Long> {

    List<OpportunitySkill> findByOpportunityId(Long opportunityId);

    List<OpportunitySkill> findBySkillId(Long skillId);

    List<OpportunitySkill> findByOpportunityIdAndIsRequired(
        Long opportunityId,
        boolean required
    );
}

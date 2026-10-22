package com.buddy.opportunity.service;

import com.buddy.development.entity.Skill;
import com.buddy.development.repository.SkillRepository;
import com.buddy.opportunity.dto.OpportunitySkillAssignmentRequest;
import com.buddy.opportunity.dto.OpportunitySkillResponse;
import com.buddy.opportunity.entity.Opportunity;
import com.buddy.opportunity.entity.OpportunitySkill;
import com.buddy.opportunity.repository.OpportunityRepository;
import com.buddy.opportunity.repository.OpportunitySkillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OpportunitySkillService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunitySkillRepository opportunitySkillRepository;
    private final SkillRepository skillRepository;

    public OpportunitySkillService(
            OpportunityRepository opportunityRepository,
            OpportunitySkillRepository opportunitySkillRepository,
            SkillRepository skillRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.opportunitySkillRepository = opportunitySkillRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public List<OpportunitySkillResponse> replaceSkills(
            Long opportunityId,
            OpportunitySkillAssignmentRequest request
    ) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Opportunity not found"
                ));

        List<OpportunitySkillAssignmentRequest.SkillAssignment> requestedSkills =
                request.skills();

        Set<Long> skillIds = new HashSet<>();

        for (OpportunitySkillAssignmentRequest.SkillAssignment assignment
                : requestedSkills) {

            if (!skillIds.add(assignment.skillId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate skill ID: " + assignment.skillId()
                );
            }
        }

        Map<Long, Skill> skillsById = new HashMap<>();

        for (Skill skill : skillRepository.findAllById(skillIds)) {
            skillsById.put(skill.getId(), skill);
        }

        if (skillsById.size() != skillIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more skill IDs do not exist"
            );
        }

        List<OpportunitySkill> newAssignments = new ArrayList<>();

        for (OpportunitySkillAssignmentRequest.SkillAssignment assignment
                : requestedSkills) {

            OpportunitySkill opportunitySkill = new OpportunitySkill();
            opportunitySkill.setOpportunity(opportunity);
            opportunitySkill.setSkill(skillsById.get(assignment.skillId()));
            opportunitySkill.setRequired(assignment.required());

            newAssignments.add(opportunitySkill);
        }

        List<OpportunitySkill> existingAssignments =
                opportunitySkillRepository.findByOpportunityId(opportunityId);

        opportunitySkillRepository.deleteAll(existingAssignments);
        opportunitySkillRepository.flush();

        List<OpportunitySkill> savedAssignments =
                opportunitySkillRepository.saveAll(newAssignments);

        return savedAssignments.stream()
                .map(assignment -> new OpportunitySkillResponse(
                        assignment.getSkill().getId(),
                        assignment.getSkill().getName(),
                        assignment.getSkill().getCategory(),
                        assignment.isRequired()
                ))
                .toList();
    }
}
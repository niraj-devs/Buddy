package com.buddy.opportunity.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.development.entity.StudentSkill;
import com.buddy.development.repository.StudentSkillRepository;
import com.buddy.opportunity.dto.OpportunitySkillMatchResponse;
import com.buddy.opportunity.entity.Opportunity;
import com.buddy.opportunity.entity.OpportunitySkill;
import com.buddy.opportunity.repository.OpportunityRepository;
import com.buddy.opportunity.repository.OpportunitySkillRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OpportunitySkillMatchService {

    private static final String COMPLETED = "COMPLETED";

    private final OpportunityRepository opportunityRepository;
    private final OpportunitySkillRepository opportunitySkillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public OpportunitySkillMatchService(
            OpportunityRepository opportunityRepository,
            OpportunitySkillRepository opportunitySkillRepository,
            StudentSkillRepository studentSkillRepository,
            UserRepository userRepository,
            StudentRepository studentRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.opportunitySkillRepository = opportunitySkillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public OpportunitySkillMatchResponse matchSkills(
            String email,
            Long opportunityId
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Authenticated user was not found"
                ));

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student profile has not been created"
                ));

        Opportunity opportunity = opportunityRepository
                .findById(opportunityId)
                .filter(Opportunity::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active opportunity not found"
                ));

        List<OpportunitySkill> requiredSkills =
                opportunitySkillRepository
                        .findByOpportunityIdAndRequired(
                                opportunityId,
                                true
                        );

        Set<Long> completedSkillIds = new HashSet<>();

        for (StudentSkill studentSkill :
                studentSkillRepository.findByStudentId(student.getId())) {

            if (COMPLETED.equals(studentSkill.getStatus())) {
                completedSkillIds.add(
                        studentSkill.getSkill().getId()
                );
            }
        }

        List<String> matchedSkills = requiredSkills.stream()
                .filter(item -> completedSkillIds.contains(
                        item.getSkill().getId()
                ))
                .map(item -> item.getSkill().getName())
                .toList();

        List<String> missingSkills = requiredSkills.stream()
                .filter(item -> !completedSkillIds.contains(
                        item.getSkill().getId()
                ))
                .map(item -> item.getSkill().getName())
                .toList();

        int total = requiredSkills.size();
        int matched = matchedSkills.size();

        BigDecimal percentage = total == 0
                ? BigDecimal.ZERO.setScale(2)
                : BigDecimal.valueOf(matched)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                BigDecimal.valueOf(total),
                                2,
                                RoundingMode.HALF_UP
                        );

        return new OpportunitySkillMatchResponse(
                opportunity.getId(),
                total,
                matched,
                matchedSkills,
                missingSkills,
                percentage
        );
    }
}

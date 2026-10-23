package com.buddy.opportunity.service;

import com.buddy.auth.entity.User;
import com.buddy.auth.repository.UserRepository;
import com.buddy.opportunity.dto.OpportunityEligibilityResponse;
import com.buddy.opportunity.entity.Opportunity;
import com.buddy.opportunity.repository.OpportunityRepository;
import com.buddy.student.entity.Student;
import com.buddy.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpportunityEligibilityService {

    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public OpportunityEligibilityService(
            OpportunityRepository opportunityRepository,
            UserRepository userRepository,
            StudentRepository studentRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public OpportunityEligibilityResponse checkEligibility(
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

        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Opportunity not found"
                ));

        List<String> reasons = new ArrayList<>();

        if (!opportunity.isActive()) {
            reasons.add("This opportunity is no longer active");
        }

        if (opportunity.getMinCgpa() != null) {
            if (student.getCgpa() == null) {
                reasons.add("Update your CGPA to confirm eligibility");
            } else if (student.getCgpa().compareTo(
                    opportunity.getMinCgpa()) < 0) {
                reasons.add(
                        "Your CGPA does not meet the minimum requirement of "
                                + opportunity.getMinCgpa()
                );
            }
        }

        if (opportunity.getTargetGradYear() != null) {
            if (student.getGraduationYear() == null) {
                reasons.add(
                        "Update your graduation year to confirm eligibility"
                );
            } else if (!student.getGraduationYear().equals(
                    opportunity.getTargetGradYear())) {
                reasons.add(
                        "This opportunity targets graduation year "
                                + opportunity.getTargetGradYear()
                );
            }
        }

        if (opportunity.getDeadline() != null
                && !opportunity.getDeadline().isAfter(LocalDateTime.now())) {
            reasons.add("The application deadline has passed");
        }

        return new OpportunityEligibilityResponse(
                opportunity.getId(),
                reasons.isEmpty(),
                List.copyOf(reasons)
        );
    }
}
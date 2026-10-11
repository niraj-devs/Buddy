package com.buddy.opportunity.repository;

import com.buddy.opportunity.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    List<Application> findByOpportunityId(Long opportunityId);

    Optional<Application> findByOpportunityIdAndStudentId(
        Long opportunityId,
        Long studentId
    );

    List<Application> findByStudentIdAndStatus(
        Long studentId,
        String status
    );
}

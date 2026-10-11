package com.buddy.academic.repository;

import com.buddy.academic.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByCollegeId(Long collegeId);

    Optional<Branch> findByCollegeIdAndCode(Long collegeId, String code);
}

package com.buddy.academic.repository;

import com.buddy.academic.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {

    Optional<College> findByName(String name);

    boolean existsByName(String name);
}

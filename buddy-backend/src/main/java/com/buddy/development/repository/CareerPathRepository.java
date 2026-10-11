package com.buddy.development.repository;

import com.buddy.development.entity.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CareerPathRepository extends JpaRepository<CareerPath, Long> {

    Optional<CareerPath> findByName(String name);

    boolean existsByName(String name);
}

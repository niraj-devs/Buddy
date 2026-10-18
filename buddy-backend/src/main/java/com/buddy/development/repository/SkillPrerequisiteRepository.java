package com.buddy.development.repository;

import com.buddy.development.entity.SkillPrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillPrerequisiteRepository
        extends JpaRepository<SkillPrerequisite, Long> {

    List<SkillPrerequisite> findBySkillId(Long skillId);
}
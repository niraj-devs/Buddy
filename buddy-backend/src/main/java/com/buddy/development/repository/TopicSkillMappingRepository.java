package com.buddy.development.repository;

import com.buddy.development.entity.TopicSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicSkillMappingRepository
        extends JpaRepository<TopicSkillMapping, Long> {

    List<TopicSkillMapping> findByTopicId(Long topicId);

    List<TopicSkillMapping> findBySkillId(Long skillId);
}

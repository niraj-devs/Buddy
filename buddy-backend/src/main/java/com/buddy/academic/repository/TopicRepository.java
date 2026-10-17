package com.buddy.academic.repository;

import com.buddy.academic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByUnitIdOrderBySequenceNumber(Long unitId);

    List<Topic> findByPrerequisitesId(Long prerequisiteTopicId);
}
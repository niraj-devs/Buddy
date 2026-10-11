package com.buddy.academic.repository;

import com.buddy.academic.entity.TopicResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicResourceRepository extends JpaRepository<TopicResource, Long> {

    List<TopicResource> findByTopicId(Long topicId);
}

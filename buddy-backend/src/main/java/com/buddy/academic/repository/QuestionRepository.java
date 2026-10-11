package com.buddy.academic.repository;

import com.buddy.academic.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTopicId(Long topicId);

    List<Question> findByTopicIdAndDifficulty(
        Long topicId,
        String difficulty
    );
}

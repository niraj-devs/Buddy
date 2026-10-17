package com.buddy.academic.dto.progress;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TopicProgressResponse(
        Long topicId,
        String topicTitle,
        String status,
        BigDecimal progressPercentage,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime lastRevisedAt,
        LocalDateTime updatedAt
) {
}
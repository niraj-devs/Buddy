package com.buddy.planning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RevisionResponse(
    Long id,
    Long topicId,
    String topicTitle,
    Long progressId,
    Integer revisionNumber,
    LocalDate scheduledDate,
    String status,
    LocalDateTime completedAt,
    LocalDateTime createdAt
) {}
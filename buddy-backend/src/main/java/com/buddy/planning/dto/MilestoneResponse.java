package com.buddy.planning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MilestoneResponse(
        Long id,
        Long goalId,
        String title,
        String description,
        Integer sequenceNumber,
        LocalDate targetDate,
        String status,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
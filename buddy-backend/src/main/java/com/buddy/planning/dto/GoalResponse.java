package com.buddy.planning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalResponse(
        Long id,
        String title,
        String description,
        String goalType,
        LocalDate targetDate,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
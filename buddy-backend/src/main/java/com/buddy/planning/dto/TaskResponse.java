package com.buddy.planning.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(

    Long id,
    Long milestoneId,
    Long studentId,
    String title,
    String description,
    String priority,
    LocalDate dueDate,
    String status,
    LocalDateTime completedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {}

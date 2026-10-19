package com.buddy.planning.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTaskRequest(
    @Pattern(
        regexp = ".*\\S.*",
        message = "Title cannot be blank"
    )
    @Size(
        max = 200,
        message = "Title cannot exceed 200 characters"
    )
    String title,

    String description,

    String priority,

    @FutureOrPresent(message = "Due date cannot be in the past")
    LocalDate dueDate,

    String status
) {}
package com.buddy.planning.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateMilestoneRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        String description,

        @NotNull(message = "Sequence number is required")
        @Min(value = 1, message = "Sequence number must be at least 1")
        Integer sequenceNumber,

        @FutureOrPresent(message = "Target date cannot be in the past")
        LocalDate targetDate
) {
}
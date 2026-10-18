package com.buddy.planning.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateGoalRequest(
        @Size(min = 1, max = 200, message = "Title must contain 1-200 characters")
        String title,

        String description,

        String goalType,

        @FutureOrPresent(message = "Target date cannot be in the past")
        LocalDate targetDate,

        String status
) {
}
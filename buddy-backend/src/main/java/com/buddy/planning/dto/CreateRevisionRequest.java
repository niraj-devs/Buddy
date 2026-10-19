package com.buddy.planning.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CreateRevisionRequest(

    @NotNull(message = "Topic ID is required")
    @Positive(message = "Topic ID must be positive")
    Long topicId,

    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date cannot be in the past")
    LocalDate scheduledDate

) {}
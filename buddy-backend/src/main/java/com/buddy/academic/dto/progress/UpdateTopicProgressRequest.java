package com.buddy.academic.dto.progress;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateTopicProgressRequest(
        @NotNull(message = "Progress percentage is required")
        @DecimalMin(value = "0.00", message = "Progress cannot be below 0")
        @DecimalMax(value = "100.00", message = "Progress cannot exceed 100")
        BigDecimal progressPercentage
) {
}
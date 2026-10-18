package com.buddy.development.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateSkillProgressRequest(
        @NotNull(message = "Progress percentage is required")
        @DecimalMin(value = "0.0", message = "Progress cannot be below 0")
        @DecimalMax(value = "100.0", message = "Progress cannot exceed 100")
        BigDecimal progressPercentage
) {
}

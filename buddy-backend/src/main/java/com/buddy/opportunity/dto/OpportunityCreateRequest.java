package com.buddy.opportunity.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OpportunityCreateRequest(

        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        @Size(max = 200)
        String organization,

        String description,

        @NotBlank
        String opportunityType,

        @Size(max = 200)
        String location,

        String workMode,

        @DecimalMin("0.00")
        @DecimalMax("10.00")
        BigDecimal minCgpa,

        @PositiveOrZero
        Short targetGradYear,

        @Size(max = 1000)
        String applicationUrl,

        @Future
        LocalDateTime deadline

) {
}

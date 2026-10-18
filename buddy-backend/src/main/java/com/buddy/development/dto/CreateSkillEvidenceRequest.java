package com.buddy.development.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateSkillEvidenceRequest(
        @NotBlank(message = "Evidence type is required")
        @Pattern(
                regexp = "PROJECT|GITHUB|CERTIFICATION|ACADEMIC|ASSESSMENT|MANUAL",
                message = "Invalid evidence type"
        )
        String evidenceType,

        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        String description,

        @Size(max = 1000)
        String referenceUrl,

        LocalDate evidenceDate
) {
}
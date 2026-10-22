package com.buddy.opportunity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OpportunitySkillAssignmentRequest(

        @NotEmpty(message = "At least one skill must be provided")
        List<@NotNull @Valid SkillAssignment> skills

) {
    public record SkillAssignment(

            @NotNull(message = "Skill ID is required")
            @Positive(message = "Skill ID must be positive")
            Long skillId,

            @NotNull(message = "Required flag must be provided")
            Boolean required

    ) {
    }
}
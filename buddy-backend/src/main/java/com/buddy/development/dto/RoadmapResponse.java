package com.buddy.development.dto;

public record RoadmapResponse(
        Long id,
        Long careerPathId,
        String name,
        String description,
        Integer version
) {}
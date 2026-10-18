package com.buddy.development.dto;

public record RoadmapSkillResponse(
        Long skillId,
        String name,
        String category,
        String description,
        Integer sequenceOrder,
        boolean core
) {}
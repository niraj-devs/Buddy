package com.buddy.academic.dto;

public record TopicResponse(
        Long id,
        String title,
        String description,
        Short sequenceNumber
) {
}

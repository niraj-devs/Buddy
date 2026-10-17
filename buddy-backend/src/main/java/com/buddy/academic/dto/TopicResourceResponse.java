package com.buddy.academic.dto;

import java.time.LocalDateTime;

public record TopicResourceResponse(
        Long id,
        String resourceType,
        String title,
        String url,
        String description,
        LocalDateTime createdAt
) {
}

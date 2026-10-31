package com.buddy.academic.dto.progress;

import java.math.BigDecimal;

public record AcademicProgressSummaryResponse(
        long totalTopics,
        long completedTopics,
        BigDecimal progressPercentage
) {
}

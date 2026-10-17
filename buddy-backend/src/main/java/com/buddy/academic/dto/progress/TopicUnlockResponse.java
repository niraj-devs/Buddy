package com.buddy.academic.dto.progress;

import java.math.BigDecimal;
import java.util.List;

public record TopicUnlockResponse(
        Long topicId,
        String topicTitle,
        String status,
        BigDecimal progressPercentage,
        boolean unlocked,
        List<String> incompletePrerequisites
) {
}
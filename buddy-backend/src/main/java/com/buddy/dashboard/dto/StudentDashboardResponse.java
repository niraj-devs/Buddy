package com.buddy.dashboard.dto;

public record StudentDashboardResponse(
        int topicsTracked,
        int topicsCompleted,
        int topicsInProgress,
        int skillsTracked,
        int skillsCompleted,
        int skillsInProgress,
        int evidenceSubmitted,
        int evidencePending
) {
}
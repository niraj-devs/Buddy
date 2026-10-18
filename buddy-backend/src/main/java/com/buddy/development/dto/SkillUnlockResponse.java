package com.buddy.development.dto;

import java.util.List;

public record SkillUnlockResponse(
        Long skillId,
        String skillName,
        boolean unlocked,
        List<String> unmetPrerequisites
) {
}
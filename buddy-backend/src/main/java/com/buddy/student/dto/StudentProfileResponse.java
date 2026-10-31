
        package com.buddy.student.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StudentProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String collegeName,
        String collegeCity,
        String collegeState,
        String branchName,
        String branchCode,
        Short semesterNumber,
        String careerPathName,
        String rollNumber,
        BigDecimal cgpa,
        Short graduationYear,
        String bio,
        String profilePictureUrl,
        String linkedinUrl,
        String githubUrl,
        String portfolioUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long collegeId,
        Long branchId,
        Long semesterId
) {
}


        package com.buddy.student.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateStudentProfileRequest(

        @Pattern(
                regexp = ".*\\S.*",
                message = "First name cannot be blank"
        )
        String firstName,

        @Pattern(
                regexp = ".*\\S.*",
                message = "Last name cannot be blank"
        )
        String lastName,

        @DecimalMin(value = "0.00", message = "CGPA cannot be below 0")
        @DecimalMax(value = "10.00", message = "CGPA cannot exceed 10")
        BigDecimal cgpa,

        @Min(value = 2000, message = "Graduation year must be 2000 or later")
        Short graduationYear,

        @Size(max = 500, message = "Bio cannot exceed 500 characters")
        String bio,

        @Size(max = 2048, message = "Profile picture URL is too long")
        String profilePictureUrl,

        @Size(max = 2048, message = "LinkedIn URL is too long")
        String linkedinUrl,

        @Size(max = 2048, message = "GitHub URL is too long")
        String githubUrl,

        @Size(max = 2048, message = "Portfolio URL is too long")
        String portfolioUrl
) {
}

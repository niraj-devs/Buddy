package com.buddy.student.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateStudentProfileRequest(

        @NotNull(message = "College ID is required")
        @Positive(message = "College ID must be positive")
        Long collegeId,

        @NotNull(message = "Branch ID is required")
        @Positive(message = "Branch ID must be positive")
        Long branchId,

        @NotNull(message = "Semester ID is required")
        @Positive(message = "Semester ID must be positive")
        Long semesterId,

        @NotNull(message = "Career path ID is required")
        @Positive(message = "Career path ID must be positive")
        Long careerPathId,

        @NotBlank(message = "Roll number is required")
        @Size(max = 50, message = "Roll number cannot exceed 50 characters")
        String rollNumber,

        @DecimalMin(value = "0.00", message = "CGPA cannot be below 0")
        @DecimalMax(value = "10.00", message = "CGPA cannot exceed 10")
        BigDecimal cgpa,

        @NotNull(message = "Graduation year is required")
        @Min(value = 2000, message = "Graduation year must be 2000 or later")
        Short graduationYear
) {}
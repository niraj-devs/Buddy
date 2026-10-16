package com.buddy.student.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

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
        Short graduationYear
) {
}
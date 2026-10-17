package com.buddy.academic.dto;

import java.math.BigDecimal;

public record SubjectResponse(
        Long id,
        String name,
        String code,
        BigDecimal credits
) {
}

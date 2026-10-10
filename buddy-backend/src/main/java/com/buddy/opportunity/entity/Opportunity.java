package com.buddy.opportunity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunities")
@Getter
@Setter
@NoArgsConstructor
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String organization;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "opportunity_type", nullable = false, length = 30)
    private String opportunityType;

    @Column(length = 200)
    private String location;

    @Column(name = "work_mode", length = 20)
    private String workMode;

    @Column(name = "min_cgpa", precision = 4, scale = 2)
    private BigDecimal minCgpa;

    @Column(name = "target_grad_year")
    private Short targetGradYear;

    @Column(name = "application_url", length = 1000)
    private String applicationUrl;

    @Column
    private LocalDateTime deadline;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

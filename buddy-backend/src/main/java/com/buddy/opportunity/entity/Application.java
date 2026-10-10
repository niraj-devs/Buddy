package com.buddy.opportunity.entity;

import com.buddy.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "applications",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_applications_opportunity_student",
            columnNames = {"opportunity_id", "student_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "opportunity_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_applications_opportunity")
    )
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_applications_student")
    )
    private Student student;

    @Column(nullable = false, length = 30)
    private String status = "APPLIED";

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
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

package com.buddy.development.entity;

import com.buddy.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_skills",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_student_skills_student_skill",
            columnNames = {"student_id", "skill_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_student_skills_student")
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "skill_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_student_skills_skill")
    )
    private Skill skill;

    @Column(nullable = false, length = 20)
    private String status = "NOT_STARTED";

    @Column(name = "progress_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Column(name = "proficiency_level", length = 20)
    private String proficiencyLevel;

    @Column(nullable = false, length = 30)
    private String source = "SELF_REPORTED";

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "achieved_at")
    private LocalDateTime achievedAt;

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

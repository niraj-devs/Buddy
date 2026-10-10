package com.buddy.opportunity.entity;

import com.buddy.development.entity.Skill;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "opportunity_skills",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_opportunity_skills_opportunity_skill",
            columnNames = {"opportunity_id", "skill_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class OpportunitySkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "opportunity_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_opportunity_skills_opportunity")
    )
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "skill_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_opportunity_skills_skill")
    )
    private Skill skill;

    @Column(name = "is_required", nullable = false)
    private boolean required = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

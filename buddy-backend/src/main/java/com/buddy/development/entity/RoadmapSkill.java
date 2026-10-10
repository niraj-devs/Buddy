package com.buddy.development.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "roadmap_skills",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_roadmap_skills_roadmap_skill",
            columnNames = {"roadmap_id", "skill_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class RoadmapSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "roadmap_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_roadmap_skills_roadmap")
    )
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "skill_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_roadmap_skills_skill")
    )
    private Skill skill;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "is_core", nullable = false)
    private boolean core = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

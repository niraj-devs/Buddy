package com.buddy.development.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "skill_prerequisites",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_skill_prerequisites_pair",
            columnNames = {"skill_id", "prerequisite_skill_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class SkillPrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "skill_id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_skill_prerequisites_skill"
        )
    )
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "prerequisite_skill_id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_skill_prerequisites_prerequisite"
        )
    )
    private Skill prerequisiteSkill;
}
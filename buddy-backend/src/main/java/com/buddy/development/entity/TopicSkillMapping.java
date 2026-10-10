package com.buddy.development.entity;

import com.buddy.academic.entity.Topic;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "topic_skill_mappings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_topic_skill_mappings_topic_skill",
            columnNames = {"topic_id", "skill_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class TopicSkillMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "topic_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_topic_skill_mappings_topic")
    )
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "skill_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_topic_skill_mappings_skill")
    )
    private Skill skill;

    @Column(
        name = "weight_percentage",
        nullable = false,
        precision = 5,
        scale = 2
    )
    private BigDecimal weightPercentage;
}

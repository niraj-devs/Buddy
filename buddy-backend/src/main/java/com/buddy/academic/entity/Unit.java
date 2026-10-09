package com.buddy.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "units",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_units_subject_number",
            columnNames = {"subject_id", "unit_number"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "subject_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_units_subject")
    )
    private Subject subject;

    @Column(name = "unit_number", nullable = false)
    private Short unitNumber;

    @Column(nullable = false, length = 200)
    private String title;
}

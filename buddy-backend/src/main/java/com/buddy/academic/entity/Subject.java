package com.buddy.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "subjects",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_subjects_semester_code",
            columnNames = {"semester_id", "code"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "semester_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_subjects_semester")
    )
    private Semester semester;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal credits;
}

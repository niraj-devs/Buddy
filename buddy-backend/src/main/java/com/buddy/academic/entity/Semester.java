package com.buddy.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "semesters",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_semesters_branch_number",
            columnNames = {"branch_id", "semester_number"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_semesters_branch")
    )
    private Branch branch;

    @Column(name = "semester_number", nullable = false)
    private Short semesterNumber;
}

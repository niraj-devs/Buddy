package com.buddy.student.entity;

import com.buddy.academic.entity.Section;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "representative_assignments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_representative_assignment",
            columnNames = {"student_id", "section_id", "academic_year"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class RepresentativeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_representative_assignments_student")
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "section_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_representative_assignments_section")
    )
    private Section section;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

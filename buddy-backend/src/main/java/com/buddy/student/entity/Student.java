package com.buddy.student.entity;

import com.buddy.academic.entity.Branch;
import com.buddy.academic.entity.College;
import com.buddy.academic.entity.Semester;
import com.buddy.auth.entity.User;
import com.buddy.development.entity.CareerPath;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "students",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_students_user",
            columnNames = "user_id"
        ),
        @UniqueConstraint(
            name = "uk_students_roll_number",
            columnNames = "roll_number"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_students_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "college_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_students_college")
    )
    private College college;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "branch_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_students_branch")
    )
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "semester_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_students_semester")
    )
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "career_path_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_students_career_path")
    )
    private CareerPath careerPath;

    @Column(name = "roll_number", nullable = false, length = 50, unique = true)
    private String rollNumber;

    @Column(precision = 4, scale = 2)
    private BigDecimal cgpa;

    @Column(name = "graduation_year", nullable = false)
    private Short graduationYear;

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

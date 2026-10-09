package com.buddy.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "sections",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_sections_semester_name",
            columnNames = {"semester_id", "name"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "semester_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_sections_semester")
    )
    private Semester semester;

    @Column(nullable = false, length = 50)
    private String name;
}

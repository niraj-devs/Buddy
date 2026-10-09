package com.buddy.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "branches",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_branches_college_code",
            columnNames = {"college_id", "code"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "college_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_branches_college")
    )
    private College college;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 20)
    private String code;
}

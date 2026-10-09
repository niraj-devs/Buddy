package com.buddy.academic.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pyq_details")
@Getter
@Setter
@NoArgsConstructor
public class PyqDetails {

    @Id
    @Column(name = "question_id")
    private Long questionId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(
        name = "question_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_pyq_details_question")
    )
    private Question question;

    @Column(name = "exam_year", nullable = false)
    private Short examYear;

    @Column(name = "exam_session", length = 50)
    private String examSession;

    @Column(length = 200)
    private String university;

    @Column(name = "paper_code", length = 50)
    private String paperCode;
}

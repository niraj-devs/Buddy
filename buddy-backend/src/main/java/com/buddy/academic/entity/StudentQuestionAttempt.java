package com.buddy.academic.entity;

import com.buddy.auth.entity.User;
import com.buddy.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_question_attempts")
@Getter
@Setter
@NoArgsConstructor
public class StudentQuestionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_student_question_attempts_student")
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "question_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_student_question_attempts_question")
    )
    private Question question;

    @Column(name = "student_answer", columnDefinition = "TEXT")
    private String studentAnswer;

    @Column(name = "is_correct")
    private Boolean correct;

    @Column(name = "marks_obtained", precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "attempted_at", nullable = false, updatable = false)
    private LocalDateTime attemptedAt;

    @PrePersist
    protected void onCreate() {
        attemptedAt = LocalDateTime.now();
    }
}

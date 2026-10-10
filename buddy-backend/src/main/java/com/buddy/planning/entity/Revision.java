package com.buddy.planning.entity;

import com.buddy.academic.entity.StudentTopicProgress;
import com.buddy.academic.entity.Topic;
import com.buddy.student.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "revisions",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_revisions_progress_number",
            columnNames = {"progress_id", "revision_number"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Revision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "progress_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_revisions_progress")
    )
    private StudentTopicProgress progress;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_revisions_student")
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "topic_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_revisions_topic")
    )
    private Topic topic;

    @Column(name = "revision_number", nullable = false)
    private Integer revisionNumber;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(nullable = false, length = 20)
    private String status = "SCHEDULED";

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

CREATE TABLE revisions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    progress_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    revision_number INT NOT NULL,
    scheduled_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_revisions
        PRIMARY KEY (id),

    CONSTRAINT uk_revisions_progress_number
        UNIQUE (progress_id, revision_number),

    CONSTRAINT chk_revisions_number
        CHECK (revision_number >= 1),

    CONSTRAINT chk_revisions_status
        CHECK (
            status IN (
                'SCHEDULED',
                'COMPLETED',
                'MISSED'
            )
        ),

    CONSTRAINT fk_revisions_progress
        FOREIGN KEY (progress_id)
        REFERENCES student_topic_progress(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_revisions_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_revisions_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE
);

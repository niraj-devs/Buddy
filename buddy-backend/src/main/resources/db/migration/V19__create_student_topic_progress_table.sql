CREATE TABLE student_topic_progress (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    last_revised_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_student_topic_progress
        PRIMARY KEY (id),

    CONSTRAINT uk_student_topic_progress
        UNIQUE (student_id, topic_id),

    CONSTRAINT chk_student_topic_progress_status
        CHECK (
            status IN (
                'NOT_STARTED',
                'IN_PROGRESS',
                'COMPLETED'
            )
        ),

    CONSTRAINT chk_student_topic_progress_percentage
        CHECK (
            progress_percentage BETWEEN 0 AND 100
        ),

    CONSTRAINT fk_student_topic_progress_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_topic_progress_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE
);

CREATE TABLE tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    milestone_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    due_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_tasks
        PRIMARY KEY (id),

    CONSTRAINT chk_tasks_priority
        CHECK (
            priority IN (
                'LOW',
                'MEDIUM',
                'HIGH'
            )
        ),

    CONSTRAINT chk_tasks_status
        CHECK (
            status IN (
                'TODO',
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED'
            )
        ),

    CONSTRAINT fk_tasks_milestone
        FOREIGN KEY (milestone_id)
        REFERENCES milestones(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_tasks_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE
);

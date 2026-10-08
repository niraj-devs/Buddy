CREATE TABLE milestones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    goal_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    sequence_number INT NOT NULL,
    target_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_milestones
        PRIMARY KEY (id),

    CONSTRAINT uk_milestones_goal_sequence
        UNIQUE (goal_id, sequence_number),

    CONSTRAINT chk_milestones_sequence
        CHECK (sequence_number >= 1),

    CONSTRAINT chk_milestones_status
        CHECK (
            status IN (
                'NOT_STARTED',
                'IN_PROGRESS',
                'COMPLETED'
            )
        ),

    CONSTRAINT fk_milestones_goal
        FOREIGN KEY (goal_id)
        REFERENCES goals(id)
        ON DELETE CASCADE
);

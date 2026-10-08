CREATE TABLE goals (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    goal_type VARCHAR(30) NOT NULL,
    target_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_goals
        PRIMARY KEY (id),

    CONSTRAINT chk_goals_type
        CHECK (
            goal_type IN (
                'ACADEMIC',
                'CAREER',
                'SKILL',
                'PROJECT',
                'PERSONAL',
                'OTHER'
            )
        ),

    CONSTRAINT chk_goals_status
        CHECK (
            status IN (
                'NOT_STARTED',
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED'
            )
        ),

    CONSTRAINT fk_goals_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE
);

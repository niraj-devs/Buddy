CREATE TABLE applications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    opportunity_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    applied_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_applications
        PRIMARY KEY (id),

    CONSTRAINT uk_applications_opportunity_student
        UNIQUE (opportunity_id, student_id),

    CONSTRAINT chk_applications_status
        CHECK (
            status IN (
                'APPLIED',
                'UNDER_REVIEW',
                'SHORTLISTED',
                'INTERVIEW',
                'SELECTED',
                'REJECTED',
                'WITHDRAWN'
            )
        ),

    CONSTRAINT fk_applications_opportunity
        FOREIGN KEY (opportunity_id)
        REFERENCES opportunities(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_applications_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE
);

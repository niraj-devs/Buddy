CREATE TABLE skill_evidence (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    evidence_type VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    reference_url VARCHAR(1000),
    evidence_date DATE,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_skill_evidence
        PRIMARY KEY (id),

    CONSTRAINT chk_skill_evidence_type
        CHECK (
            evidence_type IN (
                'ACADEMIC',
                'PROJECT',
                'ASSESSMENT',
                'GITHUB',
                'CERTIFICATION',
                'MANUAL'
            )
        ),

    CONSTRAINT chk_skill_evidence_verification
        CHECK (
            verification_status IN (
                'PENDING',
                'VERIFIED',
                'REJECTED'
            )
        ),

    CONSTRAINT fk_skill_evidence_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_skill_evidence_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

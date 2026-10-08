CREATE TABLE student_skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0,
    proficiency_level VARCHAR(20),
    source VARCHAR(30) NOT NULL DEFAULT 'SELF_REPORTED',
    started_at TIMESTAMP NULL,
    achieved_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_student_skills
        PRIMARY KEY (id),

    CONSTRAINT uk_student_skills_student_skill
        UNIQUE (student_id, skill_id),

    CONSTRAINT chk_student_skills_status
        CHECK (
            status IN (
                'NOT_STARTED',
                'IN_PROGRESS',
                'COMPLETED'
            )
        ),

    CONSTRAINT chk_student_skills_percentage
        CHECK (
            progress_percentage BETWEEN 0 AND 100
        ),

    CONSTRAINT chk_student_skills_proficiency
        CHECK (
            proficiency_level IS NULL
            OR proficiency_level IN (
                'BEGINNER',
                'INTERMEDIATE',
                'ADVANCED'
            )
        ),

    CONSTRAINT chk_student_skills_source
        CHECK (
            source IN (
                'SELF_REPORTED',
                'ACADEMIC',
                'PROJECT',
                'ASSESSMENT',
                'VERIFIED_EVIDENCE'
            )
        ),

    CONSTRAINT fk_student_skills_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_skills_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

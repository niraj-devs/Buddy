CREATE TABLE subjects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    semester_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(30) NOT NULL,
    credits DECIMAL(3,1) NOT NULL,

    CONSTRAINT pk_subjects
        PRIMARY KEY (id),

    CONSTRAINT uk_subjects_semester_code
        UNIQUE (semester_id, code),

    CONSTRAINT chk_subjects_credits
        CHECK (credits > 0),

    CONSTRAINT fk_subjects_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id)
        ON DELETE CASCADE
);

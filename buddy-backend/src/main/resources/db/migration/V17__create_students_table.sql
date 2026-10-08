CREATE TABLE students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    college_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    career_path_id BIGINT NOT NULL,
    roll_number VARCHAR(50) NOT NULL,
    cgpa DECIMAL(4,2),
    graduation_year SMALLINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_students
        PRIMARY KEY (id),

    CONSTRAINT uk_students_user
        UNIQUE (user_id),

    CONSTRAINT uk_students_roll_number
        UNIQUE (roll_number),

    CONSTRAINT chk_students_cgpa
        CHECK (cgpa IS NULL OR (cgpa >= 0 AND cgpa <= 10)),

    CONSTRAINT chk_students_graduation_year
        CHECK (graduation_year >= 2000),

    CONSTRAINT fk_students_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_students_college
        FOREIGN KEY (college_id)
        REFERENCES colleges(id),

    CONSTRAINT fk_students_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id),

    CONSTRAINT fk_students_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id),

    CONSTRAINT fk_students_career_path
        FOREIGN KEY (career_path_id)
        REFERENCES career_paths(id)
);

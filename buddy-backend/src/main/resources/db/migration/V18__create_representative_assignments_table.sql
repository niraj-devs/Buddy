CREATE TABLE representative_assignments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_representative_assignments
        PRIMARY KEY (id),

    CONSTRAINT uk_representative_assignment
        UNIQUE (student_id, section_id, academic_year),

    CONSTRAINT fk_representative_assignments_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_representative_assignments_section
        FOREIGN KEY (section_id)
        REFERENCES sections(id)
        ON DELETE CASCADE
);

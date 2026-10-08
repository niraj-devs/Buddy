CREATE TABLE sections (
    id BIGINT NOT NULL AUTO_INCREMENT,
    semester_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    
    CONSTRAINT pk_sections
        PRIMARY KEY (id),

    CONSTRAINT uk_sections_semester_name
        UNIQUE (semester_id, name),

    CONSTRAINT fk_sections_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id)
        ON DELETE CASCADE
);

CREATE TABLE semesters (
    id BIGINT NOT NULL AUTO_INCREMENT,
    branch_id BIGINT NOT NULL,
    semester_number SMALLINT NOT NULL,

    CONSTRAINT pk_semesters
        PRIMARY KEY (id),

    CONSTRAINT uk_semesters_branch_number
        UNIQUE (branch_id, semester_number),

    CONSTRAINT chk_semesters_number
        CHECK (semester_number BETWEEN 1 AND 12),

    CONSTRAINT fk_semesters_branch
        FOREIGN KEY (branch_id)
        REFERENCES branches(id)
        ON DELETE CASCADE
);

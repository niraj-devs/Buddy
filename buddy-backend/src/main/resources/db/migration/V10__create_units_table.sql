CREATE TABLE units (
    id BIGINT NOT NULL AUTO_INCREMENT,
    subject_id BIGINT NOT NULL,
    unit_number SMALLINT NOT NULL,
    title VARCHAR(200) NOT NULL,

    CONSTRAINT pk_units
        PRIMARY KEY (id),

    CONSTRAINT uk_units_subject_number
        UNIQUE (subject_id, unit_number),

    CONSTRAINT chk_units_number
        CHECK (unit_number >= 1),

    CONSTRAINT fk_units_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(id)
        ON DELETE CASCADE
);

CREATE TABLE topics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    unit_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    sequence_number SMALLINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_topics
        PRIMARY KEY (id),

    CONSTRAINT uk_topics_unit_sequence
        UNIQUE (unit_id, sequence_number),

    CONSTRAINT chk_topics_sequence
        CHECK (sequence_number >= 1),

    CONSTRAINT fk_topics_unit
        FOREIGN KEY (unit_id)
        REFERENCES units(id)
        ON DELETE CASCADE
);

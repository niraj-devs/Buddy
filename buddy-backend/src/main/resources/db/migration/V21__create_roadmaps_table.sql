CREATE TABLE roadmaps (
    id BIGINT NOT NULL AUTO_INCREMENT,
    career_path_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    version INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_roadmaps
        PRIMARY KEY (id),

    CONSTRAINT uk_roadmaps_career_path_version
        UNIQUE (career_path_id, version),

    CONSTRAINT chk_roadmaps_version
        CHECK (version >= 1),

    CONSTRAINT fk_roadmaps_career_path
        FOREIGN KEY (career_path_id)
        REFERENCES career_paths(id)
        ON DELETE CASCADE
);

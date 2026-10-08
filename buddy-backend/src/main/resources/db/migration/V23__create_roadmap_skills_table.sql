CREATE TABLE roadmap_skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    roadmap_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    sequence_order INT NOT NULL,
    is_core BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_roadmap_skills
        PRIMARY KEY (id),

    CONSTRAINT uk_roadmap_skills_roadmap_skill
        UNIQUE (roadmap_id, skill_id),

    CONSTRAINT chk_roadmap_skills_sequence
        CHECK (sequence_order >= 1),

    CONSTRAINT fk_roadmap_skills_roadmap
        FOREIGN KEY (roadmap_id)
        REFERENCES roadmaps(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_roadmap_skills_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

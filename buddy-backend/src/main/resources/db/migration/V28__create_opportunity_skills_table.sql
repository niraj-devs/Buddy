CREATE TABLE opportunity_skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    opportunity_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_opportunity_skills
        PRIMARY KEY (id),

    CONSTRAINT uk_opportunity_skills_opportunity_skill
        UNIQUE (opportunity_id, skill_id),

    CONSTRAINT fk_opportunity_skills_opportunity
        FOREIGN KEY (opportunity_id)
        REFERENCES opportunities(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_opportunity_skills_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

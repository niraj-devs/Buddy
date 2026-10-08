CREATE TABLE topic_skill_mappings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    weight_percentage DECIMAL(5,2) NOT NULL,

    CONSTRAINT pk_topic_skill_mappings
        PRIMARY KEY (id),

    CONSTRAINT uk_topic_skill_mappings_topic_skill
        UNIQUE (topic_id, skill_id),

    CONSTRAINT chk_topic_skill_mappings_weight
        CHECK (
            weight_percentage > 0
            AND weight_percentage <= 100
        ),

    CONSTRAINT fk_topic_skill_mappings_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_topic_skill_mappings_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

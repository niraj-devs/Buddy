CREATE TABLE topic_prerequisites (
    topic_id BIGINT NOT NULL,
    prerequisite_topic_id BIGINT NOT NULL,

    CONSTRAINT pk_topic_prerequisites
        PRIMARY KEY (topic_id, prerequisite_topic_id),

    CONSTRAINT fk_topic_prerequisites_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_topic_prerequisites_prerequisite
        FOREIGN KEY (prerequisite_topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_topic_prerequisites_not_self
        CHECK (topic_id <> prerequisite_topic_id)
);

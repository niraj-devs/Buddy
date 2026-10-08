CREATE TABLE topic_resources (
    id BIGINT NOT NULL AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    resource_type VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_topic_resources
        PRIMARY KEY (id),

    CONSTRAINT chk_topic_resources_type
        CHECK (
            resource_type IN (
                'VIDEO',
                'ARTICLE',
                'DOCUMENT',
                'NOTES',
                'OTHER'
            )
        ),

    CONSTRAINT fk_topic_resources_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE
);

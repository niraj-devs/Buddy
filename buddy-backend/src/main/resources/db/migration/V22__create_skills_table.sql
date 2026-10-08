CREATE TABLE skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(30) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_skills
        PRIMARY KEY (id),

    CONSTRAINT uk_skills_name
        UNIQUE (name),

    CONSTRAINT chk_skills_category
        CHECK (
            category IN (
                'LANGUAGE',
                'FRAMEWORK',
                'LIBRARY',
                'DATABASE',
                'TOOL',
                'CONCEPT',
                'PLATFORM'
            )
        )
);

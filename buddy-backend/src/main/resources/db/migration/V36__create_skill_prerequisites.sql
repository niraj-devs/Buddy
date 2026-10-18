CREATE TABLE skill_prerequisites (
    id BIGINT NOT NULL AUTO_INCREMENT,
    skill_id BIGINT NOT NULL,
    prerequisite_skill_id BIGINT NOT NULL,

    CONSTRAINT pk_skill_prerequisites
        PRIMARY KEY (id),

    CONSTRAINT uk_skill_prerequisites_pair
        UNIQUE (skill_id, prerequisite_skill_id),

    CONSTRAINT chk_skill_prerequisites_not_self
        CHECK (skill_id <> prerequisite_skill_id),

    CONSTRAINT fk_skill_prerequisites_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_skill_prerequisites_prerequisite
        FOREIGN KEY (prerequisite_skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

INSERT INTO skill_prerequisites (skill_id, prerequisite_skill_id)
SELECT skill.id, prerequisite.id
FROM (
    SELECT 'Object-Oriented Programming' AS skill_name,
           'Java' AS prerequisite_name
    UNION ALL SELECT 'Data Structures and Algorithms', 'Object-Oriented Programming'
    UNION ALL SELECT 'Maven', 'Java'
    UNION ALL SELECT 'JDBC', 'Java'
    UNION ALL SELECT 'JDBC', 'SQL'
    UNION ALL SELECT 'Spring Core', 'Object-Oriented Programming'
    UNION ALL SELECT 'Spring Boot', 'Spring Core'
    UNION ALL SELECT 'Spring Boot', 'Maven'
    UNION ALL SELECT 'REST API Development', 'Spring Boot'
    UNION ALL SELECT 'Spring Data JPA', 'JDBC'
    UNION ALL SELECT 'Spring Data JPA', 'Spring Boot'
    UNION ALL SELECT 'Spring Security', 'Spring Boot'
    UNION ALL SELECT 'Testing', 'Java'
    UNION ALL SELECT 'Testing', 'Spring Boot'
    UNION ALL SELECT 'Docker', 'Maven'
    UNION ALL SELECT 'Docker', 'Spring Boot'
    UNION ALL SELECT 'System Design', 'Data Structures and Algorithms'
    UNION ALL SELECT 'System Design', 'REST API Development'
) AS seed
JOIN skills skill ON skill.name = seed.skill_name
JOIN skills prerequisite ON prerequisite.name = seed.prerequisite_name
WHERE NOT EXISTS (
    SELECT 1
    FROM skill_prerequisites existing
    WHERE existing.skill_id = skill.id
      AND existing.prerequisite_skill_id = prerequisite.id
);
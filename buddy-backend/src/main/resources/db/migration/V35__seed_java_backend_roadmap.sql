INSERT INTO skills (name, category, description)
SELECT seed.name, seed.category, seed.description
FROM (
    SELECT 'Java' AS name, 'LANGUAGE' AS category,
           'Core Java syntax, collections, exceptions, and modern Java features.' AS description
    UNION ALL SELECT 'Object-Oriented Programming', 'CONCEPT',
           'Encapsulation, inheritance, polymorphism, and abstraction.'
    UNION ALL SELECT 'Data Structures and Algorithms', 'CONCEPT',
           'Core data structures, algorithm analysis, and problem solving.'
    UNION ALL SELECT 'SQL', 'DATABASE',
           'Relational queries, joins, aggregation, constraints, and transactions.'
    UNION ALL SELECT 'Git', 'TOOL',
           'Version control, branching, merging, and collaboration.'
    UNION ALL SELECT 'Maven', 'TOOL',
           'Java dependency management and build lifecycle.'
    UNION ALL SELECT 'JDBC', 'LIBRARY',
           'Database connectivity using Java and prepared statements.'
    UNION ALL SELECT 'Spring Core', 'FRAMEWORK',
           'Dependency injection, inversion of control, and Spring beans.'
    UNION ALL SELECT 'Spring Boot', 'FRAMEWORK',
           'Building and configuring production-ready Spring applications.'
    UNION ALL SELECT 'REST API Development', 'CONCEPT',
           'HTTP methods, status codes, resource design, and API contracts.'
    UNION ALL SELECT 'Spring Data JPA', 'FRAMEWORK',
           'Repository abstractions, ORM integration, and database persistence.'
    UNION ALL SELECT 'Spring Security', 'FRAMEWORK',
           'Authentication, authorization, and application security.'
    UNION ALL SELECT 'Testing', 'CONCEPT',
           'Unit and integration testing with JUnit and Mockito.'
    UNION ALL SELECT 'Docker', 'TOOL',
           'Containerizing applications and managing development environments.'
    UNION ALL SELECT 'System Design', 'CONCEPT',
           'Scalability, caching, reliability, and backend architecture.'
) AS seed
WHERE NOT EXISTS (
    SELECT 1 FROM skills existing WHERE existing.name = seed.name
);

INSERT INTO roadmaps (
    career_path_id, name, description, version, is_active
)
SELECT cp.id,
       'Java Backend Developer Roadmap',
       'A structured learning path from Java fundamentals to production-oriented backend engineering.',
       1,
       TRUE
FROM career_paths cp
WHERE cp.name = 'Java Backend Developer'
  AND NOT EXISTS (
      SELECT 1
      FROM roadmaps r
      WHERE r.career_path_id = cp.id
        AND r.version = 1
  );

INSERT INTO roadmap_skills (
    roadmap_id, skill_id, sequence_order, is_core
)
SELECT r.id, s.id, seed.sequence_order, TRUE
FROM (
    SELECT 'Java' AS skill_name, 1 AS sequence_order
    UNION ALL SELECT 'Object-Oriented Programming', 2
    UNION ALL SELECT 'Data Structures and Algorithms', 3
    UNION ALL SELECT 'SQL', 4
    UNION ALL SELECT 'Git', 5
    UNION ALL SELECT 'Maven', 6
    UNION ALL SELECT 'JDBC', 7
    UNION ALL SELECT 'Spring Core', 8
    UNION ALL SELECT 'Spring Boot', 9
    UNION ALL SELECT 'REST API Development', 10
    UNION ALL SELECT 'Spring Data JPA', 11
    UNION ALL SELECT 'Spring Security', 12
    UNION ALL SELECT 'Testing', 13
    UNION ALL SELECT 'Docker', 14
    UNION ALL SELECT 'System Design', 15
) AS seed
JOIN skills s ON s.name = seed.skill_name
JOIN roadmaps r ON r.name = 'Java Backend Developer Roadmap'
JOIN career_paths cp ON cp.id = r.career_path_id
WHERE cp.name = 'Java Backend Developer'
  AND r.version = 1
  AND NOT EXISTS (
      SELECT 1
      FROM roadmap_skills existing
      WHERE existing.roadmap_id = r.id
        AND existing.skill_id = s.id
  );

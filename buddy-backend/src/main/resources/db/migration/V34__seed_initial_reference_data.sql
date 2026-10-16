INSERT INTO colleges (name, city, state)
VALUES ('Sri Venkateswara College of Engineering and Technology', 'Chittoor', 'Andhra Pradesh');

INSERT INTO branches (college_id, name, code)
SELECT c.id, 'Computer Science and Engineering', 'CSE'
FROM colleges c
WHERE c.name = 'Sri Venkateswara College of Engineering and Technology'
  AND NOT EXISTS (
      SELECT 1 FROM branches b
      WHERE b.college_id = c.id AND b.code = 'CSE'
  );

INSERT INTO semesters (branch_id, semester_number)
SELECT b.id, s.semester_number
FROM branches b
JOIN colleges c ON c.id = b.college_id
CROSS JOIN (
    SELECT 1 AS semester_number
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
    UNION ALL SELECT 5
    UNION ALL SELECT 6
    UNION ALL SELECT 7
    UNION ALL SELECT 8
) s
WHERE c.name = 'Sri Venkateswara College of Engineering and Technology'
  AND b.code = 'CSE'
  AND NOT EXISTS (
      SELECT 1 FROM semesters existing
      WHERE existing.branch_id = b.id
        AND existing.semester_number = s.semester_number
  );

INSERT INTO career_paths (name, description, is_active)
SELECT 'Java Backend Developer',
       'A learning path focused on Java, Spring Boot, databases, REST APIs, testing, and backend engineering.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM career_paths
    WHERE name = 'Java Backend Developer'
);

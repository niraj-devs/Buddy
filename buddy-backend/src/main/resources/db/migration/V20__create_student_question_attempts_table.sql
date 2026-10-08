CREATE TABLE student_question_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    student_answer TEXT,
    is_correct BOOLEAN,
    marks_obtained DECIMAL(5,2),
    attempt_number INT NOT NULL,
    time_taken_seconds INT,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_student_question_attempts
        PRIMARY KEY (id),

    CONSTRAINT chk_student_question_attempts_number
        CHECK (attempt_number >= 1),

    CONSTRAINT chk_student_question_attempts_time
        CHECK (
            time_taken_seconds IS NULL
            OR time_taken_seconds >= 0
        ),

    CONSTRAINT chk_student_question_attempts_marks
        CHECK (
            marks_obtained IS NULL
            OR marks_obtained >= 0
        ),

    CONSTRAINT fk_student_question_attempts_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student_question_attempts_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id)
        ON DELETE CASCADE
);

CREATE TABLE pyq_details (
    question_id BIGINT NOT NULL,
    exam_year SMALLINT NOT NULL,
    exam_session VARCHAR(50),
    university VARCHAR(200),
    paper_code VARCHAR(50),

    CONSTRAINT pk_pyq_details
        PRIMARY KEY (question_id),

    CONSTRAINT chk_pyq_exam_year
        CHECK (exam_year >= 1900),

    CONSTRAINT fk_pyq_details_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id)
        ON DELETE CASCADE
);

CREATE TABLE questions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    topic_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(30) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    marks DECIMAL(5,2) NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    answer TEXT,
    explanation TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_questions
        PRIMARY KEY (id),

    CONSTRAINT chk_questions_type
        CHECK (
            question_type IN (
                'MCQ',
                'SHORT_ANSWER',
                'LONG_ANSWER',
                'CODING',
                'DESCRIPTIVE'
            )
        ),

    CONSTRAINT chk_questions_difficulty
        CHECK (
            difficulty IN (
                'EASY',
                'MEDIUM',
                'HARD'
            )
        ),

    CONSTRAINT chk_questions_source
        CHECK (
            source_type IN (
                'QUESTION_BANK',
                'PYQ'
            )
        ),

    CONSTRAINT chk_questions_marks
        CHECK (marks > 0),

    CONSTRAINT fk_questions_topic
        FOREIGN KEY (topic_id)
        REFERENCES topics(id)
        ON DELETE CASCADE
);

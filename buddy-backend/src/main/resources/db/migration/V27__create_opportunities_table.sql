CREATE TABLE opportunities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    organization VARCHAR(200) NOT NULL,
    description TEXT,
    opportunity_type VARCHAR(30) NOT NULL,
    location VARCHAR(200),
    work_mode VARCHAR(20),
    min_cgpa DECIMAL(4,2),
    target_grad_year SMALLINT,
    application_url VARCHAR(1000),
    deadline TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_opportunities
        PRIMARY KEY (id),

    CONSTRAINT chk_opportunities_type
        CHECK (
            opportunity_type IN (
                'INTERNSHIP',
                'JOB',
                'HACKATHON',
                'SCHOLARSHIP',
                'COMPETITION',
                'OTHER'
            )
        ),

    CONSTRAINT chk_opportunities_work_mode
        CHECK (
            work_mode IS NULL
            OR work_mode IN (
                'ONSITE',
                'REMOTE',
                'HYBRID'
            )
        ),

    CONSTRAINT chk_opportunities_cgpa
        CHECK (
            min_cgpa IS NULL
            OR (min_cgpa >= 0 AND min_cgpa <= 10)
        ),

    CONSTRAINT chk_opportunities_grad_year
        CHECK (
            target_grad_year IS NULL
            OR target_grad_year >= 2000
        )
);

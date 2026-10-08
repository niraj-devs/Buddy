CREATE TABLE branches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    college_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(20) NOT NULL,

    CONSTRAINT pk_branches
        PRIMARY KEY (id),

    CONSTRAINT uk_branches_college_code
        UNIQUE (college_id, code),

    CONSTRAINT fk_branches_college
        FOREIGN KEY (college_id)
        REFERENCES colleges(id)
        ON DELETE CASCADE
);

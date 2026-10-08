CREATE TABLE colleges (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),

    CONSTRAINT pk_colleges
        PRIMARY KEY (id),

    CONSTRAINT uk_colleges_name
        UNIQUE (name)
);

CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,

    CONSTRAINT pk_roles
        PRIMARY KEY (id),

    CONSTRAINT uk_roles_name
        UNIQUE (name)
);

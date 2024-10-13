CREATE SEQUENCE IF NOT EXISTS users_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE users
(
    id       BIGINT NOT NULL,
    login    VARCHAR(20),
    email    VARCHAR(50),
    password VARCHAR(120),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uc_users_email UNIQUE (email),
    CONSTRAINT uc_users_login UNIQUE (login)
);


CREATE TABLE IF NOT EXISTS users
(
    id            SERIAL                 NOT NULL,
    email         CHARACTER VARYING(255) NOT NULL,
    password_hash CHARACTER VARYING(255) NOT NULL,
    role          CHARACTER VARYING(1)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT email_unique UNIQUE (email)
);
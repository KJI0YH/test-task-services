CREATE TABLE IF NOT EXISTS role
(
    id SERIAL NOT NULL,
    name CHARACTER VARYING(50) NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id)
);

INSERT INTO role (id, name) VALUES (1, 'ADMIN');
INSERT INTO role (id, name) VALUES (2, 'USER');

CREATE TABLE IF NOT EXISTS users
(
    id SERIAL NOT NULL,
    email CHARACTER VARYING(255) NOT NULL,
    password CHARACTER VARYING(255) NOT NULL,
    role_id INTEGER NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT email_unique UNIQUE (email),
    CONSTRAINT role_id_fkey FOREIGN KEY (role_id)
        REFERENCES role (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

INSERT INTO users (email, password, role_id) VALUES ('admin@admin.com', '1000:568a5bc8f0b3beb119dd2009aed25eb1:58dec998464c2336824f6ac083630a2404238e493eb1ed283eed131fab107ad7eb59e0d5391b983c070cff0b0b15239ac373776a2d2af90cae975acef66c9386', 1);
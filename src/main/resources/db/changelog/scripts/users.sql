-- liquibase formatted sql

-- changeset ezuykow:1
CREATE TABLE users
(
    user_id          SERIAL PRIMARY KEY,
    email            VARCHAR(50) NOT NULL UNIQUE,
    encoded_password VARCHAR(255) NOT NULL,
    first_name       VARCHAR(50) NOT NULL,
    last_name        VARCHAR(50) NOT NULL,
    phone            VARCHAR(20) NOT NULL,
    role             VARCHAR(50) NOT NULL,
    image            TEXT
);



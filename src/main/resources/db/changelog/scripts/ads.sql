-- liquibase formatted sql

-- changeset ezuykow:1
CREATE TABLE ads
(
    ad_id       SERIAL PRIMARY KEY,
    author_id   INT  NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    image       TEXT NOT NULL,
    price       INT  NOT NULL,
    title       VARCHAR(100),
    description TEXT
);

-- liquibase formatted sql

-- changeset ezuykow:1
CREATE TABLE ads
(
    ad_id       SERIAL PRIMARY KEY,
    author_id   INT  NOT NULL,
    image       TEXT NOT NULL,
    price       INT  NOT NULL,
    title       VARCHAR(100),
    description TEXT
);

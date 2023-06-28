-- liquibase formatted sql

-- changeset ezuykow:1
CREATE TABLE comments
(
    comment_id    SERIAL PRIMARY KEY,
    ad_id         INT    NOT NULL,
    author_id     INT    NOT NULL,
    creating_time BIGINT NOT NULL,
    comment_text  TEXT   NOT NULL
);

CREATE SCHEMA IF NOT EXISTS movie_service_schema;
CREATE TABLE IF NOT EXISTS movie_service_schema.movies
(
    id          UUID PRIMARY KEY,
    title       VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    duration    INTEGER NOT NULL
);
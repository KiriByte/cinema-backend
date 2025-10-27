CREATE SCHEMA IF NOT EXISTS session_service_schema;
CREATE TABLE IF NOT EXISTS session_service_schema.sessions
(
    id         UUID PRIMARY KEY,
    movie_id   UUID          NOT NULL,
    hall_id    BIGINT        NOT NULL,
    start_time TIMESTAMPTZ     NOT NULL,
    end_time   TIMESTAMPTZ     NOT NULL,
    price      NUMERIC(5, 2) NOT NULL CHECK ( price >= 0 ),
    status     VARCHAR       NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
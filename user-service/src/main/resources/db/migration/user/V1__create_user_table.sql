CREATE SCHEMA IF NOT EXISTS user_service_schema;
CREATE TABLE IF NOT EXISTS user_service_schema.app_users
(
    user_id    BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS auth_service_schema.refresh_tokens
(
    token_id   BIGSERIAL PRIMARY KEY,
    user_id    BIGSERIAL NOT NULL,
    token      VARCHAR(255),
    expired_at TIMESTAMP
);
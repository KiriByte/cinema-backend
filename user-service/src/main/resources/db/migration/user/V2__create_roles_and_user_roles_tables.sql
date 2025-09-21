CREATE TABLE user_service_schema.roles(
    role_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_service_schema.user_roles(
    user_id BIGSERIAL NOT NULL,
    role_id BIGSERIAL NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
)
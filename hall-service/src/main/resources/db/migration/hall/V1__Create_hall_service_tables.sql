CREATE SCHEMA IF NOT EXISTS hall_service_schema;

-- Создание таблицы seat_type
CREATE TABLE hall_service_schema.seat_type
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- Создание таблицы halls
CREATE TABLE hall_service_schema.halls
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT
);

-- Создание таблицы seats
CREATE TABLE hall_service_schema.seats
(
    id           BIGSERIAL PRIMARY KEY,
    hall_id      BIGINT NOT NULL,
    row_number   BIGINT NOT NULL,
    seat_number  BIGINT NOT NULL,
    seat_type_id BIGINT,

    CONSTRAINT fk_seat_hall
        FOREIGN KEY (hall_id)
            REFERENCES hall_service_schema.halls (id),

    CONSTRAINT fk_seat_type
        FOREIGN KEY (seat_type_id)
            REFERENCES hall_service_schema.seat_type (id)
);
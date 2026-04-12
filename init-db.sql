CREATE DATABASE location_db;
CREATE DATABASE user_db;
CREATE DATABASE vehicle_db;
CREATE DATABASE driver_db;



CREATE DATABASE auth_db;
\c auth_db;

CREATE TABLE roles (
  id SERIAL PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);

CREATE TABLE accounts (
  id SERIAL PRIMARY KEY,
  phone VARCHAR(20) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role_id INT NOT NULL,
  status VARCHAR(20),
  CONSTRAINT fk_role FOREIGN KEY(role_id) REFERENCES roles(id)
);

-- Chèn dữ liệu mẫu
INSERT INTO roles (id, name) VALUES (1, 'USER'), (2, 'DRIVER');
INSERT INTO accounts (phone, password, role_id, status)
VALUES ('0123456789', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 1, 'ACTIVE'),
       ('9876543210', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7u41W3u', 2, 'ACTIVE');


CREATE DATABASE keycloak_db;

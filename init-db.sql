-- Create databases
CREATE DATABASE vehicle_db;
CREATE DATABASE booking_db;
CREATE DATABASE payment_db;
CREATE DATABASE trip_db;
CREATE DATABASE user_db;
CREATE DATABASE driver_db;
CREATE DATABASE auth_db;
CREATE DATABASE notification_db;
CREATE DATABASE location_db;
CREATE DATABASE keycloak_db;

-- VEHICLE-SERVICE DATA
\c vehicle_db;
CREATE TABLE IF NOT EXISTS vehicle_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    number_of_seat INT,
    price_per_km DECIMAL
);
CREATE TABLE IF NOT EXISTS vehicles (
    id SERIAL PRIMARY KEY,
    plate VARCHAR(255),
    color VARCHAR(255),
    vehicle_type_id INT REFERENCES vehicle_types(id),
    owner_id BIGINT
);
INSERT INTO vehicle_types (id, name, number_of_seat, price_per_km) VALUES 
(1, 'Standard 4-Seat', 4, 15000.0),
(2, 'XL 7-Seat', 7, 25000.0)
ON CONFLICT (id) DO NOTHING;
INSERT INTO vehicles (plate, color, vehicle_type_id, owner_id) VALUES 
('51A-12345', 'Black', 1, 9876543210),
('51B-67890', 'White', 2, 9876543210);

-- PAYMENT-SERVICE DATA
\c payment_db;
CREATE TABLE IF NOT EXISTS payment_methods (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(50),
    is_active BOOLEAN
);
INSERT INTO payment_methods (name, code, is_active) VALUES 
('Tiền mặt', 'CASH', true),
('Thanh toán Online', 'ONLINE', true);

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount DECIMAL NOT NULL,
    currency VARCHAR(10) NOT NULL,
    stripe_session_id VARCHAR(255),
    stripe_payment_intent_id VARCHAR(255),
    gateway_response_data TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_id VARCHAR(255),
    aggregate_type VARCHAR(255),
    event_type VARCHAR(255),
    payload TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inbox (
    message_id VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP
);

-- USER-SERVICE DATA
\c user_db;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    full_name VARCHAR(255),
    phone_number VARCHAR(15),
    email VARCHAR(255),
    address TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
INSERT INTO users (id, full_name, phone_number, email, address, created_at, updated_at) VALUES 
(123456789, 'Truong User', '0123456789', 'user@example.com', 'Ho Chi Minh City', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- AUTH-SERVICE DATA
\c auth_db;
CREATE TABLE IF NOT EXISTS roles (
  id SERIAL PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
  id SERIAL PRIMARY KEY,
  phone VARCHAR(20) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role_id INT NOT NULL,
  status VARCHAR(20),
  CONSTRAINT fk_role FOREIGN KEY(role_id) REFERENCES roles(id)
);

INSERT INTO roles (id, name) VALUES (1, 'USER'), (2, 'DRIVER') ON CONFLICT (id) DO NOTHING;
INSERT INTO accounts (phone, password, role_id, status) VALUES 
('0123456789', '$2a$10$h.BAn5Sl7BAuSnyIptZzLut1oZf19AnE.pNp7mUXVsh8In8H4IasG', 1, 'ACTIVE'),
('9876543210', '$2a$10$h.BAn5Sl7BAuSnyIptZzLut1oZf19AnE.pNp7mUXVsh8In8H4IasG', 2, 'ACTIVE')
ON CONFLICT DO NOTHING;

\c booking_db;
CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_id VARCHAR(255),
    aggregate_type VARCHAR(255),
    event_type VARCHAR(255),
    payload TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inbox (
    message_id VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP
);

-- Database schema for the Asset Management App.
-- Reconstructed from the SQL queries in HelloController.java: the original
-- schema was not saved, so column types, sizes and foreign key rules are
-- inferred from how the code uses them.

CREATE DATABASE IF NOT EXISTS `skuska2.2` CHARACTER SET utf8mb4;
USE `skuska2.2`;

CREATE TABLE asset_types (
    type_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE rooms (
    number INT PRIMARY KEY,
    area INT NOT NULL
);

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL
);

CREATE TABLE assets (
    inventory_number VARCHAR(50) PRIMARY KEY,
    asset_status ENUM('IN_USE', 'BROKEN', 'LOST', 'AVAILABLE') NOT NULL,
    type_id INT NOT NULL,
    room_number INT NOT NULL,
    responsible_employee_id INT NOT NULL,
    FOREIGN KEY (type_id) REFERENCES asset_types (type_id),
    FOREIGN KEY (room_number) REFERENCES rooms (number),
    FOREIGN KEY (responsible_employee_id) REFERENCES employees (id)
);

CREATE TABLE services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    inventory_number VARCHAR(50) NOT NULL,
    service_date DATE NOT NULL,
    service_success BOOLEAN NOT NULL,
    FOREIGN KEY (inventory_number) REFERENCES assets (inventory_number)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- Sample data for trying the app.
INSERT INTO asset_types (name) VALUES ('Laptop'), ('Monitor');
INSERT INTO rooms (number, area) VALUES (101, 20), (102, 35);
INSERT INTO employees (full_name) VALUES ('Test Employee A'), ('Test Employee B');
INSERT INTO assets (inventory_number, asset_status, type_id, room_number, responsible_employee_id) VALUES
    ('INV-001', 'IN_USE', 1, 101, 1),
    ('INV-002', 'AVAILABLE', 2, 102, 2);

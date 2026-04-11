# Driver Service

## Overview

Driver Service là một **Entity Service** quản lý hồ sơ và trạng thái hoạt động của Tài xế trong hệ thống TrangSM.

### Trách nhiệm chính:
- Quản lý thông tin hồ sơ tài xế (Tên, SĐT, Bằng lái, v.v.).
- Quản lý trạng thái hiện tại của tài xế (AVAILABLE, ON_TRIP, OFFLINE).
- Cập nhật trạng thái tài xế thông qua Kafka sự kiện.

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 3.4.x  |
| Database   | PostgreSQL         |
| Messaging  | Apache Kafka       |
| Build Tool | Maven              |

## API Endpoints

| Method | Endpoint                    | Description                                  |
|--------|-----------------------------|----------------------------------------------|
| GET    | `/health`                   | Kiểm tra trạng thái hoạt động (JSON)         |
| GET    | `/api/v1/drivers/{id}`      | Lấy thông tin chi tiết một tài xế            |

## Kafka Consumers

| Topic                | Event                       | Action                         |
|----------------------|-----------------------------|--------------------------------|
| `driver-status-topic`| `DriverStatusChangeEvent`   | Cập nhật trạng thái tài xế mới |

## environment Variables

Cấu hình trong `.env` tại thư mục gốc:

| Variable                 | Description                         | Default                                 |
|--------------------------|-------------------------------------|-----------------------------------------|
| `SPRING_DATASOURCE_URL`  | JDBC URL cho PostgreSQL             | `jdbc:postgresql://postgres-db:5432/driver_db` |
| `SPRING_DATASOURCE_USER` | Username DB                         | `postgres`                              |
| `SPRING_DATASOURCE_PASS` | Password DB                         | `root`                                  |
| `KAFKA_SERVERS`           | Kafka bootstrap servers            | `kafka-server:29092`                   |
| `DRIVER_SERVICE_PORT`    | Cổng bên ngoài của service          | `8083`                                  |

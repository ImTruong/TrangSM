# Vehicle Service

## Overview

Vehicle Service là một **Entity Service** chịu trách nhiệm quản lý thông tin về các loại xe và phương tiện trong hệ thống TrangSM. Service này cung cấp biểu giá cơ bản và thông tin định danh phương tiện cho các Task Service khác.

### Trách nhiệm chính:
- Quản lý danh mục loại xe (VehicleType) và giá cước cơ bản.
- Quản lý thông tin chi tiết từng phương tiện (Vehicle).

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 3.4.x  |
| Database   | PostgreSQL         |
| Build Tool | Maven              |

## API Endpoints

| Method | Endpoint                    | Description                                  |
|--------|-----------------------------|----------------------------------------------|
| GET    | `/health`                   | Kiểm tra trạng thái hoạt động của service    |
| GET    | `/api/v1/vehicle-types`     | Lấy danh sách tất cả các loại xe             |
| GET    | `/api/v1/vehicles/{id}`     | Truy xuất thông tin chi tiết của một xe      |

> Full API specification: [`docs/api-specs/vehicle-service.yaml`](../../docs/api-specs/vehicle-service.yaml)

## Running Locally

### Sử dụng Docker Compose (Khuyên dùng)
Từ thư mục gốc của dự án:
```bash
docker compose up vehicle-service --build
```
Service sẽ chạy tại: `http://localhost:8084`



## environment Variables

Cấu hình trong `.env` tại thư mục gốc hoặc `application.properties`:

| Variable                 | Description                         | Default                                 |
|--------------------------|-------------------------------------|-----------------------------------------|
| `SPRING_DATASOURCE_URL`  | JDBC URL cho PostgreSQL             | `jdbc:postgresql://postgres-db:5432/vehicle_db` |
| `SPRING_DATASOURCE_USER` | Username DB                         | `postgres`                              |
| `SPRING_DATASOURCE_PASS` | Password DB                         | `root`                                  |
| `SERVER_PORT`            | Cổng chạy service (nội bộ container) | `5000`                                  |

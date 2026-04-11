# User Service

## Overview

User Service là một **Entity Service** chịu trách nhiệm quản lý thông tin cá nhân của Khách hàng trong hệ thống TrangSM. Service này cung cấp logic agnostic (không phụ thuộc quy trình cụ thể), cho phép tái sử dụng cho nhiều nghiệp vụ khác nhau như đặt xe, quản lý tài khoản, hoặc thông báo.

### Trách nhiệm chính:
- Quản lý thông tin hồ sơ khách hàng (Tên, Số điện thoại, Ảnh đại diện).
- Cung cấp API truy xuất thông tin khách hàng cho các Task Service (như Booking Task).

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 3.4.x  |
| Database   | PostgreSQL         |
| Build Tool | Maven              |

## API Endpoints

| Method | Endpoint             | Description                                  |
|--------|----------------------|----------------------------------------------|
| GET    | `/health`           | Kiểm tra trạng thái hoạt động của service    |
| GET    | `/api/v1/user/{id}`  | Truy xuất thông tin cá nhân của Khách hàng   |

> Full API specification: [`docs/api-specs/user-service.yaml`](../../docs/api-specs/user-service.yaml)

## Running Locally

### Sử dụng Docker Compose (Khuyên dùng)
Từ thư mục gốc của dự án:
```bash
docker compose up user-service --build
```
Service sẽ chạy tại: `http://localhost:8082`



## Project Structure

```
user-service/
├── Dockerfile          # Cấu hình container hóa (Multi-stage build)
├── pom.xml             # Quản lý dependencies Maven
├── readme.md           # Tài liệu hướng dẫn
└── src/
    ├── main/java/...   # Mã nguồn Spring Boot
    └── main/resources/ # Cấu hình application.properties
```

## Environment Variables

Cấu hình trong `.env` tại thư mục gốc hoặc `application.properties`:

| Variable                 | Description                         | Default                                 |
|--------------------------|-------------------------------------|-----------------------------------------|
| `SPRING_DATASOURCE_URL`  | JDBC URL cho PostgreSQL             | `jdbc:postgresql://postgres-db:5432/user_db` |
| `SPRING_DATASOURCE_USER` | Username DB                         | `postgres`                              |
| `SPRING_DATASOURCE_PASS` | Password DB                         | `root`                                  |
| `SERVER_PORT`            | Cổng chạy service (nội bộ container) | `5000`                                  |

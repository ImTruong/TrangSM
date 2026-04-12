# Auth Service

## Overview

`auth-service` chịu trách nhiệm quản lý xác thực (Authentication) và ủy quyền (Authorization) trong hệ thống microservices TrangSM.

### Trách nhiệm chính:
- Xác thực người dùng thông qua số điện thoại và mật khẩu.
- Tích hợp với **Keycloak** để quản lý danh tính (Identity Management) và cấp phát **Access Token** (JWT).
- Quản lý thông tin tài khoản và phân quyền người dùng trong cơ sở dữ liệu nội bộ.

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 4.0.5  |
| Security   | Keycloak Admin Client, Spring Security |
| Database   | PostgreSQL         |
| Build Tool | Maven              |

## API Endpoints

| Method | Endpoint                    | Description                                  |
|--------|-----------------------------|----------------------------------------------|
| GET    | `/health`                   | Kiểm tra trạng thái hoạt động (JSON)         |
| POST   | `/api/v1/auth/login`        | Đăng nhập và nhận Access Token từ Keycloak   |

> Full API specification: [`docs/api-specs/auth-service.yaml`](../../docs/api-specs/auth-service.yaml)

## Environment Variables

Cấu hình trong `.env` tại thư mục gốc hoặc qua biến môi trường:

| Variable                     | Description                         | Default                                 |
|------------------------------|-------------------------------------|-----------------------------------------|
| `DB_URL`                     | JDBC URL cho PostgreSQL             | `jdbc:postgresql://postgres-db:5432/auth_db` |
| `DB_USER`                    | Username DB                         | `postgres` (tuỳ chỉnh)                  |
| `DB_PASSWORD`                | Password DB                         | `root` (tuỳ chỉnh)                      |
| `KEYCLOAK_AUTH_SERVER_URL`   | URL của Keycloak Server            | `http://keycloak-server:8080`           |
| `KEYCLOAK_REALM`             | Tên Realm trong Keycloak            | `trang-sm`                              |
| `KEYCLOAK_RESOURCE`          | Client ID                           | `auth-service`                          |
| `KEYCLOAK_CREDENTIALS_SECRET`| Client Secret                       | `my-client-secret`                      |
| `SERVER_PORT`                | Cổng ứng dụng                       | `5000`                                  |

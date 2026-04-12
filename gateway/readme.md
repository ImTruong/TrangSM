# API Gateway

## Overview

API Gateway đóng vai trò là điểm truy cập duy nhất (Single Entry Point) cho tất cả các yêu cầu từ phía client. Nó thực hiện điều hướng các yêu cầu (Request routing) đến các microservice tương ứng ở phía backend.

## Responsibilities

- **Request routing**: Chuyển tiếp các yêu cầu đến đúng service.
- **Authentication**: Xác thực JWT token.
- **Request/Response transformation**: Chỉnh sửa headers, truyền thêm các thông tin X-User-Id, X-User-Name, X-User-Roles.

## Tech Stack

| Component  | Choice                                   |
|------------|------------------------------------------|
| Approach   | Spring Cloud Gateway (Java 21)           |

## Routing Table

| External Path             | Target Service          | Internal URL                                       |
|---------------------------|-------------------------|----------------------------------------------------|
| `/api/auth/**`            | auth-service            | `http://auth-service:5000/api/v1/auth/*`           |
| `/api/booking/**`         | booking-service         | `http://booking-service:5000/api/v1/booking/*`     |
| `/api/user/**`            | user-service            | `http://user-service:5000/api/v1/user/*`           |
| `/api/driver/**`          | driver-service          | `http://driver-service:5000/api/v1/drivers/*`      |
| `/api/vehicle/**`         | vehicle-service         | `http://vehicle-service:5000/api/v1/vehicles/*`     |
| `/api/location/**`        | location-service        | `http://location-service:5000/api/v1/location/*`   |
| `/api/notification/**`    | notification-service     | `http://notification-service:5000/api/v1/notifications/*`|

## Running

```bash
# Chạy từ thư mục gốc của dự án
docker compose up gateway --build
```

## Configuration

Gateway sử dụng mạng lưới (networking) của Docker Compose. Các service có thể truy cập được thông qua tên service được định nghĩa trong file `docker-compose.yml` (ví dụ: `auth-service`, `driver-service`).

## Notes

- Sử dụng tên service (thay vì `localhost`) cho các URL upstream bên trong Docker.
- Gateway công khai cổng **8080** (External) và lắng nghe tại cổng **8000** (Internal).
- Authentication được xử lý thông qua tích hợp Keycloak (OAuth2 Resource Server).

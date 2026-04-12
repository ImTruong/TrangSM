# Location Service

## Overview

`location-service` là một **Utility Service** chịu trách nhiệm quản lý vị trí thời gian thực của các phương tiện (tài xế) và thực hiện các truy vấn tìm kiếm theo khoảng cách (Geospatial search) trong hệ thống TrangSM.

### Trách nhiệm chính:
- Tiếp nhận và cập nhật vị trí thời gian thực của tài xế vào **Redis GEO**.
- Tìm kiếm danh sách các tài xế khả dụng (AVAILABLE) gần một tọa độ nhất định.
- Đồng bộ trạng thái khả dụng của tài xế thông qua Kafka khi tài xế chấp nhận chuyến đi.
- Sử dụng cơ chế Buffer để tối ưu hóa việc ghi dữ liệu vị trí dữ liệu.

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 4.0.5  |
| Database   | PostgreSQL         |
| Cache/Geo  | Redis              |
| Messaging  | Apache Kafka       |
| Build Tool | Maven              |

## API Endpoints

| Method | Endpoint                    | Description                                  |
|--------|-----------------------------|----------------------------------------------|
| GET    | `/health`                   | Kiểm tra trạng thái hoạt động (JSON)         |
| POST   | `/api/v1/location/update`   | Cập nhật vị trí và trạng thái của tài xế     |
| GET    | `/api/v1/location/nearby`   | Tìm kiếm tài xế gần nhất trong bán kính      |

> Full API specification: [`docs/api-specs/location-service.yaml`](../../docs/api-specs/location-service.yaml)

## Kafka Consumers

| Topic             | Event                 | Action                                      |
|-------------------|-----------------------|---------------------------------------------|
| `driver.accepted` | `DriverAcceptedEvent` | Xóa tài xế khỏi danh sách khả dụng (GEO)    |

## Environment Variables

Cấu hình thông qua biến môi trường hoặc file `.env`:

| Variable                 | Description                         | Default                                 |
|--------------------------|-------------------------------------|-----------------------------------------|
| `DB_URL`                 | JDBC URL cho PostgreSQL             | `jdbc:postgresql://postgres-db:5432/location_db` |
| `DB_USER`                | Username DB                         | `postgres`                              |
| `DB_PASSWORD`            | Password DB                         | `root`                                  |
| `SPRING_DATA_REDIS_HOST` | Host của Redis server               | `redis-server`                          |
| `SPRING_DATA_REDIS_PORT` | Port của Redis server               | `6379`                                  |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS`| Kafka bootstrap servers      | `kafka-server:29092`                    |
| `SERVER_PORT`            | Cổng ứng dụng                       | `5000`                                  |
| `APP_BUFFER_MAX_SIZE`    | Kích thước buffer vị trí            | `500`                                   |
| `APP_BUFFER_FLUSH_INTERVAL`| Khoảng thời gian flush buffer     | `5000` (ms)                             |

# Trip Service

## Overview

Trip Service là một **Entity Service** chịu trách nhiệm quản lý toàn bộ vòng đời của một chuyến đi trong hệ thống TrangSM, từ lúc khởi tạo đến khi hoàn thành hoặc bị hủy.

### Trách nhiệm chính:
- Lưu trữ và quản lý thông tin chuyến đi (Khách hàng, Tài xế, Xe, Tọa độ, Giá tiền).
- Cập nhật trạng thái chuyến đi dựa trên các sự kiện từ hệ thống.
- Đảm bảo tính nhất quán dữ liệu và độ tin cậy của thông báo thông qua **Transactional Outbox Pattern**.

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 3.4.x  |
| Database   | PostgreSQL         |
| Messaging  | Apache Kafka       |
| Build Tool | Maven              |

## API Endpoints

| Method | Endpoint            | Description                                  |
|--------|---------------------|----------------------------------------------|
| GET    | `/health`           | Kiểm tra trạng thái hoạt động (JSON)         |
| GET    | `/trips/{id}`       | Lấy thông tin chi tiết một chuyến đi         |

## Kafka Consumers

Dịch vụ lắng nghe các sự kiện sau để điều phối trạng thái chuyến đi:

| Topic                    | Action                                                                 |
|--------------------------|------------------------------------------------------------------------|
| `trip-request-topic`      | Khởi tạo chuyến đi mới (PENDING) và lưu snapshot thông tin khách hàng. |
| `payment-success-topic`  | Cập nhật trạng thái thành `FINDING_DRIVER` sau khi thanh toán thành công. |
| `driver-accepted-topic`  | Cập nhật thông tin tài xế, xe snapshot và chuyển trạng thái sang `ACCEPTED`. |
| `trip-started-topic`     | Cập nhật thời gian bắt đầu và trạng thái `STARTED`.                    |
| `trip-completed-topic`   | Cập nhật thời gian kết thúc và trạng thái `COMPLETED`.                 |
| `trip-cancelled-topic`   | Cập nhật lý do hủy, thời gian hủy và trạng thái `CANCELLED`.           |

## Kafka Producers (via Outbox)

Sau mỗi lần cập nhật trạng thái thành công, Trip Service sẽ đẩy các sự kiện phản hồi lên Kafka để Orchestrator tiếp tục xử lý:
- `trip-created-topic`
- `trip-payment-success-response-topic`
- `trip-accepted-response-topic`
- `trip-started-response-topic`
- `trip-completed-response-topic`
- `trip-cancelled-response-topic`

## Environment Variables

Cấu hình trong `.env` hoặc `application.properties`:

| Variable                 | Description                                     | Default                                 |
|--------------------------|-------------------------------------------------|-----------------------------------------|
| `SPRING_DATASOURCE_URL`  | JDBC URL cho PostgreSQL                         | `jdbc:postgresql://db:5432/trip_db`    |
| `KAFKA_BOOTSTRAP_SERVERS`| Kafka bootstrap servers                         | `kafka-server:29092`                   |
| `TRIP_SERVICE_PORT`      | Cổng bên ngoài của service                      | `8085`                                  |
| `OUTBOX_POLLING_RATE`    | Tốc độ quét bảng outbox để đẩy lên Kafka (ms)   | `5000`                                  |

# Booking Service

## Overview

Booking Service là **Task/Orchestrator Service** điều phối toàn bộ lifecycle đặt xe bằng Kafka + Outbox + Inbox.

### Trách nhiệm chính
- Tính giá ước tính theo khoảng cách đường chim bay (Haversine) và giá/km từ Vehicle Service.
- Nhận yêu cầu đặt xe từ customer, lấy snapshot thông tin User + VehicleType.
- Publish event tạo trip (`trip-request-topic`) và tạo payment (`create-payment-topic`).
- Lắng nghe phản hồi payment/trip để cập nhật trạng thái booking.
- Điều phối notify tài xế/khách hàng và luồng accept/reject/start/complete/cancel.
- Đảm bảo idempotency ở consumer bằng Inbox Pattern.

## API Endpoints

| Method | Endpoint                          | Description |
|--------|-----------------------------------|-------------|
| GET    | `/health`                         | Health check |
| GET    | `/api/v1/bookings/estimate`       | Tính giá ước tính |
| POST   | `/api/v1/bookings`                | Tạo booking |
| POST   | `/api/v1/bookings/{id}/accept`    | Driver nhận cuốc |
| POST   | `/api/v1/bookings/{id}/reject`    | Driver từ chối cuốc |
| POST   | `/api/v1/bookings/{id}/start`     | Driver bắt đầu chuyến |
| POST   | `/api/v1/bookings/{id}/complete`  | Driver hoàn tất chuyến |
| POST   | `/api/v1/bookings/{id}/cancel`    | Customer hủy chuyến |

## Kafka Topics

### Producers (Outbox)
- `trip-request-topic`
- `create-payment-topic`
- `payment-success-topic`
- `driver-accepted-topic`
- `driver.accepted`
- `trip-started-topic`
- `trip-completed-topic`
- `trip-cancelled-topic`

### Consumers (Inbox)
- `payment-response-topic`
- `trip-payment-success-response-topic`
- `trip-accepted-response-topic`
- `trip-started-response-topic`
- `trip-completed-response-topic`
- `trip-cancelled-response-topic`

## Inter-service Calls (Docker DNS)
- `http://user-service:5000`
- `http://vehicle-service:5000`
- `http://driver-service:5000`
- `http://location-service:5000`
- `http://notification-service:5000`

## Run

```bash
cd services/booking-service
mvn clean package -DskipTests
```


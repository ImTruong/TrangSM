# Payment Service

## Overview

Payment Service là một **Entity Service** chịu trách nhiệm quản lý các giao dịch thanh toán trong hệ thống TrangSM. Nó hỗ trợ cả thanh toán bằng tiền mặt (Cash) và thanh toán trực tuyến qua cổng **Stripe**.

### Trách nhiệm chính:
- Khởi tạo và quản lý trạng thái giao dịch (PENDING, PAID, FAILED, COMPLETED).
- Tích hợp với Stripe Checkout để xử lý thanh toán online.
- Xử lý các callback (webhooks) từ Stripe để cập nhật trạng thái thanh toán theo thời gian thực.
- Đảm bảo tính nhất quán dữ liệu qua **Transactional Outbox Pattern** và tính idempotency qua **Inbox Pattern**.

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 3.4.x  |
| Database   | PostgreSQL         |
| Messaging  | Apache Kafka       |
| Gateway    | Stripe API         |
| Build Tool | Maven              |

## API Endpoints

| Method | Endpoint                    | Description                                       |
|--------|-----------------------------|---------------------------------------------------|
| GET    | `/health`                   | Kiểm tra trạng thái hoạt động (JSON)              |
| GET    | `/payments/methods`         | Lấy danh sách phương thức thanh toán hỗ trợ       |
| POST   | `/payments/webhook`         | Tiếp nhận và xử lý Webhooks từ Stripe             |

## Kafka Consumers

| Topic                   | Action                                                                              |
|-------------------------|-------------------------------------------------------------------------------------|
| `create-payment-topic`  | Khởi tạo giao dịch mới. Nếu là ONLINE, tạo Stripe Session và trả về checkout URL.    |
| `trip-completed-topic`   | Đánh dấu giao dịch CASH là `COMPLETED` sau khi chuyến đi kết thúc.                  |

## Kafka Producers (via Outbox)

| Topic                    | Action                                                               |
|--------------------------|----------------------------------------------------------------------|
| `payment-response-topic` | Gửi kết quả thanh toán (PAID/FAILED/PENDING) về cho Orchestrator.    |

## Environment Variables

| Variable                | Description                                     | Default                                    |
|-------------------------|-------------------------------------------------|--------------------------------------------|
| `STRIPE_API_KEY`        | Private Key của Stripe                          | (Required)                                 |
| `STRIPE_WEBHOOK_SECRET` | Secret để xác thực Stripe Webhook Signature     | (Required)                                 |
| `STRIPE_SUCCESS_URL`    | URL chuyển hướng khi thanh toán thành công      | `http://localhost:8080/payment/success`    |
| `STRIPE_CANCEL_URL`     | URL chuyển hướng khi thanh toán bị hủy          | `http://localhost:8080/payment/cancel`     |
| `OUTBOX_POLLING_RATE`   | Tốc độ quét bảng outbox (ms)                    | `5000`                                     |

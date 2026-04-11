# Notification Service

## Overview

Notification Service là một **Utility Service** chịu trách nhiệm đẩy thông báo thời gian thực đến Khách hàng và Tài xế thông qua **WebSocket**.

### Trách nhiệm chính:
- Duy trì kết nối WebSocket với các client.
- Nhận yêu cầu gửi thông báo từ Kafka hoặc REST API.
- Đẩy thông báo đến đúng người nhận dựa trên `recipientId`.

## Tech Stack

| Component  | Choice             |
|------------|--------------------|
| Language   | Java 21            |
| Framework  | Spring Boot 3.4.x  |
| Real-time  | Spring WebSocket   |
| Messaging  | Apache Kafka       |
| Database   | **None**           |

## API & WebSocket Endpoints

| Type      | Endpoint                    | Description                                  |
|-----------|-----------------------------|----------------------------------------------|
| GET       | `/health`                   | Kiểm tra trạng thái hoạt động (JSON)         |
| POST      | `/api/v1/notifications`     | Gửi thông báo thủ công (REST)                |
| WebSocket | `/socket?userId={id}`       | Kết nối WebSocket cho Client/Driver          |

## Kafka Consumers

| Topic                | Event                       | Action                         |
|----------------------|-----------------------------|--------------------------------|
| `notification-topic` | `NotificationMessage`       | Đẩy thông báo qua WebSocket    |

## Testing WebSocket

Bạn có thể sử dụng các công cụ như `wscat` hoặc Postman (WebSocket Request):
1. Kết nối: `ws://localhost:8088/socket?userId=user123`
2. Gửi một POST request đến `http://localhost:8088/api/v1/notifications` với body:
   ```json
   {
     "recipientId": "user123",
     "title": "Hello",
     "content": "You have a new booking!",
     "data": { "tripId": "999" }
   }
   ```
3. Bạn sẽ thấy tin nhắn xuất hiện trong cửa sổ WebSocket.

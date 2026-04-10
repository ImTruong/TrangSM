# Analysis and Design — Business Process Automation Solution

> **Goal**: Analyze a specific business process and design a service-oriented automation solution (SOA/Microservices).
> Scope: 4–6 week assignment — focus on **one business process**, not an entire system.

**References:**
1. *Service-Oriented Architecture: Analysis and Design for Services and Microservices* — Thomas Erl (2nd Edition)
2. *Microservices Patterns: With Examples in Java* — Chris Richardson
3. *Bài tập — Phát triển phần mềm hướng dịch vụ* — Hung Dang (available in Vietnamese)

---

## Part 1 — Analysis Preparation

### 1.1 Business Process Definition

Describe or diagram the high-level Business Process to be automated.

- **Domain**: Hệ thống vận chuyển hành khách
- **Business Process**: Quy trình đặt xe và vận chuyển
- **Actors**: Khách hàng, Tài xế
- **Scope**: Đăng nhập và xác thực, chọn điểm đón/trả, chọn xe, ước tính tiền, chọn phương thức thanh toán, tìm tài xế gần nhất, đánh dấu trạng thái tài xế, đánh dấu trạng thái chuyến đi, thanh toán.

**Process Diagram:**

![Process Diagram](asset/Process%20Diagram.png)

### 1.2 Existing Automation Systems

List existing systems, databases, or legacy logic related to this process.

None — the process is currently performed manually.

> If none exist, state: *"None — the process is currently performed manually."*

### 1.3 Non-Functional Requirements

Non-functional requirements serve as input for identifying Utility Service and Microservice Candidates in step 2.7.

| Requirement    | Description |
|----------------|-------------|
| Performance    | Tìm tài xế gần nhất < 10 giây. Notification đến tài xế < 2 giây.             |
| Security       | Role-based: customer không gọi được driver-endpoint và ngược lại.             |

---

## Part 2 — REST/Microservices Modeling

### 2.1 Decompose Business Process & 2.2 Filter Unsuitable Actions

Decompose the process from 1.1 into granular actions. Mark actions unsuitable for service encapsulation.

| #  | Action                               | Actor                   | Description                                                                 | Suitable? |
|----|--------------------------------------|------------------------|-----------------------------------------------------------------------------|-----------|
| 1  | Đăng nhập hệ thống                   | Khách hàng             | Xác thực danh tính, cấp JWT token                                          | ✅        |
| 2  | Chọn điểm đón                        | Khách hàng             | Nhập/chọn địa chỉ điểm đón                                     | ✅        |
| 3  | Chọn điểm trả                        | Khách hàng             | Nhập/chọn địa chỉ điểm trả                                     | ✅        |
| 4  | Chọn loại xe                         | Khách hàng             | Chọn loại xe (4 chỗ, 7 chỗ...) và xem giá ước tính                         | ✅        |
| 5  | Chọn phương thức thanh toán          | Khách hàng             | Chọn thanh toán online hoặc tiền mặt                                       | ✅        |
| 6  | Thực hiện thanh toán online          | Hệ thống / Khách hàng  | Tạo giao dịch, gọi Payment Gateway xử lý trước chuyến                      | ✅        |
| 7  | Xử lý thanh toán thất bại            | Hệ thống               | Thông báo thất bại, cho phép quay lại chọn phương thức                     | ✅        |
| 8  | Tìm tài xế phù hợp                   | Hệ thống               | Truy vấn Location Service lấy danh sách tài xế online gần điểm đón         | ✅        |
| 9  | Gửi thông báo chuyến đến tài xế      | Hệ thống               | Push notification qua WebSocket/FCM đến tài xế, chờ phản hồi              | ✅        |
| 10 | Tài xế từ chối / timeout             | Tài xế                 | Ghi nhận từ chối, hệ thống chuyển sang tài xế tiếp theo                   | ✅        |
| 11 | Tài xế chấp nhận chuyến              | Tài xế                 | Tài xế xác nhận nhận chuyến qua app                                       | ✅        |
| 12 | Cập nhật trạng thái tài xế thành bận | Hệ thống               | Đánh dấu on_trip, loại khỏi pool tìm kiếm                                 | ✅        |
| 13 | Bắt đầu chuyến đi                    | Hệ thống               | Ghi nhận thời điểm bắt đầu                     | ✅        |
| 14 | Thông báo kết quả đến khách hàng     | Hệ thống               | Push thông tin tài xế & xe đến khách qua WebSocket/FCM                    | ✅        |
| 15 | Thanh toán tiền mặt sau chuyến       | Khách hàng / Tài xế    | Trao đổi tiền mặt trực tiếp — không qua hệ thống                          | ❌        |
| 16 | Hoàn tất chuyến đi                   | Hệ thống               | Cập nhật trạng thái trip = completed                                      | ✅        |
| 17 | Cập nhật trạng thái tài xế về rảnh   | Hệ thống               | Đánh dấu available, sẵn sàng nhận chuyến mới                              | ✅        |
> Actions marked ❌: manual-only, require human judgment, or cannot be encapsulated as a service.

### 2.3 Entity Service Candidates

Identify business entities and group reusable (agnostic) actions into Entity Service Candidates.

| Entity | Service Candidate | Agnostic Capabilities (Reusable across processes) |
| :--- | :--- | :--- |
| User | User Service | Truy xuất thông tin (profile) khách hàng. |
| Driver | Driver Service | Truy xuất thông tin (profile) tài xế; Cập nhật trạng thái hoạt động của tài xế (rảnh/bận). |
| Vehicle / Vehicle Type | Vehicle Service | Truy xuất thông tin chi tiết phương tiện; Truy xuất thông tin loại hình xe; Truy xuất biểu giá cơ bản theo loại xe. |
| Trip | Trip Service | Tạo bản ghi chuyến đi; Cập nhật trạng thái chuyến đi (started/completed/cancelled); Truy xuất thông tin chuyến đi. |
| Payment | Payment Service | Tạo mới bản ghi giao dịch; Cập nhật trạng thái giao dịch (pending/paid/failed); Truy xuất thông tin giao dịch. |

### 2.4 Task Service Candidate

Group process-specific (non-agnostic) actions into a Task Service Candidate.

| Non-agnostic Action | Task Service Candidate |
|---------------------|------------------------|
| Ước tính giá tiền chuyến đi; Tìm tài xế phù hợp; Điều phối thông báo về chuyến đi cho khách và tài xế; Xử lý logic tài xế từ chối/chấp nhận; Kích hoạt chuyển đổi trạng thái chuyến đi. Điều phối trạng thái chuyến đi dựa trên kết quả payment                     |Booking Task Service (orchestrator)                       |

### 2.5 Identify Resources

Map entities/processes to REST URI Resources.

| Entity / Process | Resource URI |
|------------------|-------------|
| User             | /users/ |
| Driver           | /drivers/ |
| Vehicle          | /vehicles/ |
| Vehicle Type     | /vehicle-types/ |
| Trip             | /trips/ |
| Payment          | /payments/ |
| Booking Process  | /bookings |


### 2.6 Associate Capabilities with Resources and Methods

| Service Candidate | Capability | Resource | HTTP Method |
| :--- | :--- | :--- | :--- |
| **Auth Service** | Xác thực người dùng và cấp JWT Token | `/auth/login` | POST |
| **User Service** | Lấy thông tin profile khách hàng | `/users/{id}` | GET |
| **Driver Service** | Lấy thông tin profile tài xế | `/drivers/{id}` | GET |
| **Driver Service** | Cập nhật trạng thái tài xế (rảnh/bận) | `/drivers/{id}/status` | PATCH |
| **Vehicle Service** | Lấy thông tin chi tiết phương tiện | `/vehicles/{id}` | GET |
| **Vehicle Service** | Lấy thông tin loại xe và biểu giá | `/vehicle-types` | GET |
| **Location Service** | Tìm danh sách tài xế rảnh gần nhất | `/locations/nearby` | GET |
| **Payment Service** | Lấy danh sách phương thức thanh toán | `/payments/methods` | GET |
| **Payment Service** | Khởi tạo giao dịch thanh toán (Pending) | `/payments` | POST |
| **Trip Service** | Tạo bản ghi chuyến đi mới | `/trips` | POST |
| **Trip Service** | Cập nhật trạng thái & thời gian chuyến đi | `/trips/{id}` | PATCH |
| **Notification Service**| Gửi thông báo (Push Notification) đến thiết bị | `/notifications` | POST |
| **Booking Task Service**| Ước tính giá tiền dựa trên tọa độ và loại xe | `/bookings/estimate` | GET |
| **Booking Task Service**| Nhận yêu cầu đặt xe| `/bookings` | POST |
| **Booking Task Service**| Xử lý logic vòng đời: Tài xế nhận chuyến | `/bookings/{id}/accept` | POST |
| **Booking Task Service**| Xử lý logic vòng đời: Bắt đầu chuyến đi | `/bookings/{id}/start` | POST |
| **Booking Task Service**| Xử lý logic vòng đời: Hoàn thành chuyến đi | `/bookings/{id}/complete` | POST |

### 2.7 Utility Service & Microservice Candidates

Based on Non-Functional Requirements (1.3) and Processing Requirements, identify cross-cutting utility logic or logic requiring high autonomy/performance.

| Candidate             | Type (Utility / Microservice) | Justification                                                                 |
|----------------------|-------------------------------|-------------------------------------------------------------------------------|
| API Gateway         | Utility Service               | Đóng vai trò Single Entry Point. Định tuyến request (Routing) và kiểm tra tính hợp lệ của JWT Token (Authorization) trước khi cho phép truy cập vào các service nghiệp vụ nội bộ. |
| Auth Service         | Utility Service               | Chịu trách nhiệm xác thực thông tin đăng nhập, sinh (cấp phát) JWT Token. Tách riêng để giảm tải logic bảo mật cho các service nghiệp vụ. |
| Notification Service | Utility Service               | Bọc logic gọi API của Firebase/WebSocket (Performance NFR). Có thể dùng chung cho mọi quy trình. |
| Location Service     | Microservice                 | Cần hiệu năng truy vấn cao (Geospatial query trên Redis) để tìm tài xế < 10s (Performance NFR). Tách riêng để dễ scale. |


### 2.8 Service Composition Candidates

Interaction diagram showing how Service Candidates collaborate to fulfill the business process.

![Service Composition Candidate](asset/service%20composition%20candidate.png)

---

## Part 3 — Service-Oriented Design

> Part 3 is the **convergence point** — regardless of whether you used Step-by-Step Action or DDD in Part 2, the outputs here are the same: service contracts and service logic.

### 3.1 Uniform Contract Design

Service Contract specification for each service. Full OpenAPI specs:
- [`docs/api-specs/service-a.yaml`](api-specs/service-a.yaml)
- [`docs/api-specs/service-b.yaml`](api-specs/service-b.yaml)

> 💡 **Derive from Part 2:** Each service capability from 2.6 maps to one API endpoint. Update the OpenAPI spec files to match.

**Service 1 — Auth Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/auth/login` | POST | Xác thực người dùng (Client/Driver) và cấp phát JWT Token. | `{ "phone": "string", "password": "string" }` | `200 OK`, `401 Unauthorized` |

**Service 2 — User Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/users/{id}` | GET | Truy xuất thông tin cá nhân của khách hàng. | N/A | `200 OK`, `404 Not Found` |

**Service 3 — Driver Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/drivers/{id}` | GET | Truy xuất thông tin hồ sơ của tài xế. | N/A | `200 OK`, `404 Not Found` |
| `/drivers/{id}/status` | PATCH | Cập nhật trạng thái làm việc (rảnh/bận) của tài xế. | { "status": "AVAILABLE | ON_TRIP" } | `200 OK`, `400 Bad Request` |

**Service 4 — Vehicle Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/vehicles/{id}` | GET | Lấy thông tin chi tiết một chiếc xe (biển số, màu sắc). | N/A | `200 OK`, `404 Not Found` |
| `/vehicle-types` | GET | Lấy danh sách các loại hình xe và biểu giá cơ bản. | N/A | `200 OK` |

**Service 5 — Location Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/locations/nearby`| GET | Tìm tài xế rảnh gần nhất dựa trên tọa độ và loại xe. | { "lat": "string", "lng": "string", "vehicleTypeId": "string" } | `200 OK`, `404 Not Found` |

**Service 6 — Payment Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/payments/methods`| GET | Lấy danh sách phương thức thanh toán hỗ trợ. | N/A | `200 OK` |
| `/payments` | POST | Khởi tạo giao dịch thanh toán trạng thái Pending. | `{ "customerId": "string", "amount": number, "method": "string" }` | `201 Created`, `400 Bad Request` |

**Service 7 — Trip Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/trips` | POST | Tạo mới một bản ghi chuyến đi (Status mặc định PENDING). | `{ "paymentId": "string", "distance": number, "price": number }` | `201 Created`, `400 Bad Request` |
| `/trips/{id}` | PATCH | Cập nhật trạng thái chuyến đi (ACCEPTED, STARTED, COMPLETED). | `{ "status": "string", "driverId": "string", "updatedAt": "timestamp" }` | `200 OK`, `409 Conflict` |

**Service 8 — Notification Service:**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/notifications` | POST | Đẩy thông báo (Push notification) đến thiết bị Client/Driver. | `{ "targetId": "string", "title": "string", "body": "string", "data": {} }`| `200 OK`, `400 Bad Request` |

**Service 9 — Booking Task Service (Orchestrator):**
| Endpoint | Method | Description | Request Body | Response Codes |
|----------|--------|-------------|--------------|----------------|
| `/bookings/estimate` | GET | Ước tính khoảng cách chim bay và giá cho từng loại xe. | N/A (Query Params: `latA`, `lngA`, `latB`, `lngB`) | `200 OK`, `400 Bad Request` |
| `/bookings` | POST | Khách hàng gửi yêu cầu đặt xe, khởi tạo luồng tìm kiếm. | `{ "pickup": {}, "dropoff": {}, "vehicleTypeId": "string", "paymentMethod": "string" }` | `202 Accepted`, `400 Bad Request` |
| `/bookings/{id}/accept`| POST | Tài xế nhận cuốc, báo Notification về cho khách hàng. | `{ "driverId": "string" }` | `200 OK`, `409 Conflict` |
| `/bookings/{id}/start` | POST | Đánh dấu bắt đầu hành trình. | `{ "driverId": "string" }` | `200 OK` |
| `/bookings/{id}/complete`| POST| Hoàn tất hành trình và giải phóng tài xế. | `{ "driverId": "string" }` | `200 OK` |

### 3.2 Service Logic Design

Internal processing flow for each service.

**Service A:**

```mermaid
flowchart TD
    A[Receive Request] --> B{Validate?}
    B -->|Valid| C[(Process / DB)]
    B -->|Invalid| D[Return 4xx Error]
    C --> E[Return Response]
```

**Service B:**

```mermaid
flowchart TD
    A[Receive Request] --> B{Validate?}
    B -->|Valid| C[(Process / DB)]
    B -->|Invalid| D[Return 4xx Error]
    C --> E[Return Response]
```

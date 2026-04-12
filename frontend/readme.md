# Frontend

## Overview

Simple demo frontend with **2 separate pages**:
- `client.html`: create and track booking
- `driver.html`: receive booking signal and perform driver actions

The UI is static HTML/CSS/JS served by Nginx on port `3000`.

## Implemented Flow

### Client Page
- Pre-filled pickup/dropoff coordinates
- Load vehicle types
- Load payment methods
- Estimate fare (`GET /api/bookings/estimate`)
- Create booking (`POST /api/bookings`)
- Poll booking status (`GET /api/bookings/{id}`)
- Show checkout URL when payment service returns it

### Driver Page
- Watch booking ID
- Receive new booking signal from client page (browser localStorage event)
- Poll booking status
- Driver actions:
  - Accept: `POST /api/bookings/{id}/accept`
  - Reject: `POST /api/bookings/{id}/reject`
  - Start: `POST /api/bookings/{id}/start`
  - Complete: `POST /api/bookings/{id}/complete`

## Project Structure

```text
frontend/
├── Dockerfile
├── nginx.conf
├── readme.md
└── src/
    ├── index.html
    ├── client.html
    ├── driver.html
    └── assets/
        ├── styles.css
        ├── client.js
        └── driver.js
```

## API Proxy Mapping

Nginx proxies frontend calls to internal Docker services:
- `/api/bookings/*` -> `booking-service` (`/api/v1/bookings/*`)
- `/api/vehicles/types` -> `vehicle-service` (`/api/v1/vehicles/types`)
- `/api/payment/methods` -> `payment-service` (`/api/v1/payment/methods`)

## Run (Docker)

```bash
cd /Users/truong/year4semester2/SOA/TrangSM

docker compose up -d --build frontend booking-service vehicle-service payment-service
```

Open:
- `http://localhost:3000/`
- `http://localhost:3000/client.html`
- `http://localhost:3000/driver.html`

## Notes

- This demo UI is intentionally simple.
- Driver notification in this UI is browser-local (for demo), while backend notification service remains available in the system.
- If `frontend` service is still commented in `docker-compose.yml`, uncomment/add it before running.

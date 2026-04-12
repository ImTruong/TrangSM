#!/usr/bin/env bash
set -euo pipefail

# Minimal E2E flow for booking orchestrator through gateway.
# Prerequisites:
# 1) docker compose up --build
# 2) valid JWT tokens for customer + driver (Keycloak)
# 3) existing user/driver/vehicle/location test data

GATEWAY_URL="${GATEWAY_URL:-http://localhost:8080}"
CUSTOMER_TOKEN="${CUSTOMER_TOKEN:-eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaOVpIU0dSaFRqQTIySndlTTRyRk1xQWNQa3h5eDRUTi13aVZTYmprdDh3In0.eyJleHAiOjE3NzU5ODYzMDIsImlhdCI6MTc3NTk4NjAwMiwianRpIjoiMDlhZTk3ZjYtNTU1Ny00ODI1LTkyMjMtZjIyNzljY2RkMDIzIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXNlcnZlcjo4MDgwL3JlYWxtcy90cmFuZy1zbSIsInN1YiI6ImNmYWY5MWFmLTUwZmUtNDI4Ny1iM2U3LThhMTRiMWUwZTMxMiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImF1dGgtc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiJhOThiZTNkYi0yYzE0LTQ4NGEtOGYwMC0xZmE4MGZkZjdjMzIiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIlVTRVIiXX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImE5OGJlM2RiLTJjMTQtNDg0YS04ZjAwLTFmYTgwZmRmN2MzMiIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiVXNlciBPbmUiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiIwMTIzNDU2Nzg5IiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6Ik9uZSIsImVtYWlsIjoidXNlcjFAdHJhbmctc20uY29tIn0.Jap1YOp6gYn15ewaa8RWw_UYMABzL3V1zNV5hivAaER9-f7_BJ6uNevNyjrlNCOoc2b6b7WpRX1PtXn0Mi0JG4RwtJA3Y2bKS915-JxcAYKTKAgA_vUeOd0qnrU1Vrh4DhGsX6n8HDG7vjlXPzrzCJxzmWnIrsSS10_Q0JbFU-SNomElNAAOsHYwsmcH9OTqnT1zCsBj5aEzhFibPAWL1mzQcQ8THGP7TJc9B1gjErUfqoKIR0_G7R5DtPhkrmCy_O6_z20643XXILauIpzw_s9VbjNpYBh0aRGVW4pdsQ63YsofRsng200cun4vGzZgAB07GN8TGX9RD7S01Z1Aww}"
DRIVER_TOKEN="${DRIVER_TOKEN:-eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJaOVpIU0dSaFRqQTIySndlTTRyRk1xQWNQa3h5eDRUTi13aVZTYmprdDh3In0.eyJleHAiOjE3NzU5ODY1MTcsImlhdCI6MTc3NTk4NjIxNywianRpIjoiOGE5NzkyMzAtMGYxNS00Zjg3LWFlN2UtMzk1NGY2NDM2MWIzIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXNlcnZlcjo4MDgwL3JlYWxtcy90cmFuZy1zbSIsInN1YiI6ImM1MTk1ZmJiLWUyNDQtNGE0MC1iZTQwLTEzMDc2YTdmYzUwNyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImF1dGgtc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiIzMWI3OTczZS01YjU0LTRjMzEtODlhZC04OWY3MWUwNDgzMGUiLCJhY3IiOiIxIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIkRSSVZFUiJdfSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwic2lkIjoiMzFiNzk3M2UtNWI1NC00YzMxLTg5YWQtODlmNzFlMDQ4MzBlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJEcml2ZXIgT25lIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiOTg3NjU0MzIxMCIsImdpdmVuX25hbWUiOiJEcml2ZXIiLCJmYW1pbHlfbmFtZSI6Ik9uZSIsImVtYWlsIjoiZHJpdmVyMUB0cmFuZy1zbS5jb20ifQ.NVhRfZwRorLTm-nv_yRTfUFiSXEzLLb6UVkyKyjtpPoxadFqWFEtS52LuYhpSvv_jPFpwyJ9vWnU6_ckjXDnVINWvxlqquQ9S-FZl3wRQ94GZxX5_TAwz9PVLZ3Aq6NSbgRiG0CbalOuMFpr09VyW1Nk0hl3jZ7abWaFZea1Qbj-Va0aEPd-KJ77nvzzMwS9ElSH_PLt8aXfFCy2JjvGLeFRGlexYdGOnzhSKvISMKSEhleoTUWf5dDhmjxg45TRoJZmsHt01dwIejKcgY0GUpi-CEXIrfgJDMsJ_lQRV5kD22hzECEdN15fpfjt4y0YrChaWYy5F9Xjz7FX4_X55w}"

PICKUP_LNG="${PICKUP_LNG:-106.700981}"
PICKUP_LAT="${PICKUP_LAT:-10.776889}"
DROPOFF_LNG="${DROPOFF_LNG:-106.682171}"
DROPOFF_LAT="${DROPOFF_LAT:-10.762622}"
VEHICLE_TYPE_ID="${VEHICLE_TYPE_ID:-1}"
PAYMENT_METHOD="${PAYMENT_METHOD:-CASH}"
DRIVER_ID="${DRIVER_ID:-9876543210}"

if [[ -z "$CUSTOMER_TOKEN" || -z "$DRIVER_TOKEN" ]]; then
  echo "CUSTOMER_TOKEN and DRIVER_TOKEN are required"
  exit 1
fi

echo "[1] Estimate"
curl -sS "$GATEWAY_URL/api/bookings/estimate?pickUpLongitude=$PICKUP_LNG&pickUpLatitude=$PICKUP_LAT&dropOffLongitude=$DROPOFF_LNG&dropOffLatitude=$DROPOFF_LAT&vehicleTypeId=$VEHICLE_TYPE_ID" | jq .

echo "[2] Create booking"
CREATE_RES=$(curl -sS -X POST "$GATEWAY_URL/api/bookings" \
  -H "Authorization: Bearer $CUSTOMER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"pickUpLongitude\":$PICKUP_LNG,\"pickUpLatitude\":$PICKUP_LAT,\"dropOffLongitude\":$DROPOFF_LNG,\"dropOffLatitude\":$DROPOFF_LAT,\"vehicleTypeId\":$VEHICLE_TYPE_ID,\"paymentMethod\":\"$PAYMENT_METHOD\"}")

echo "$CREATE_RES" | jq .
BOOKING_ID=$(echo "$CREATE_RES" | jq -r '.bookingId')

if [[ "$BOOKING_ID" == "null" || -z "$BOOKING_ID" ]]; then
  echo "bookingId not found in create response"
  exit 1
fi

echo "[3] Driver accept"
curl -sS -X POST "$GATEWAY_URL/api/bookings/$BOOKING_ID/accept" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"driverId\":$DRIVER_ID}" | jq .

echo "[4] Driver start"
curl -sS -X POST "$GATEWAY_URL/api/bookings/$BOOKING_ID/start" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"driverId\":$DRIVER_ID}" | jq .

echo "[5] Driver complete"
curl -sS -X POST "$GATEWAY_URL/api/bookings/$BOOKING_ID/complete" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"driverId\":$DRIVER_ID}" | jq .

echo "Flow finished for bookingId=$BOOKING_ID"


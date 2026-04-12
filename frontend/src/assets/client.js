const els = {
  userId: document.getElementById("userId"),
  userRoles: document.getElementById("userRoles"),
  pickUpLongitude: document.getElementById("pickUpLongitude"),
  pickUpLatitude: document.getElementById("pickUpLatitude"),
  dropOffLongitude: document.getElementById("dropOffLongitude"),
  dropOffLatitude: document.getElementById("dropOffLatitude"),
  vehicleType: document.getElementById("vehicleType"),
  paymentMethod: document.getElementById("paymentMethod"),
  estimateBtn: document.getElementById("estimateBtn"),
  createBtn: document.getElementById("createBtn"),
  estimateBox: document.getElementById("estimateBox"),
  createBox: document.getElementById("createBox"),
  bookingBox: document.getElementById("bookingBox"),
  checkoutWrap: document.getElementById("checkoutWrap"),
  checkoutLink: document.getElementById("checkoutLink"),
};

let currentBookingId = null;
let pollTimer = null;

function asJson(res) {
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`);
  }
  return res.json();
}

function headers() {
  return {
    "Content-Type": "application/json",
    "X-User-Id": String(els.userId.value || ""),
    "X-User-Roles": String(els.userRoles.value || ""),
  };
}

async function loadVehicleTypes() {
  const res = await fetch("/api/vehicles/types");
  const types = await asJson(res);
  els.vehicleType.innerHTML = "";
  for (const t of types) {
    const opt = document.createElement("option");
    opt.value = t.id;
    opt.textContent = `${t.name} - ${t.pricePerKm}/km - ${t.numberOfSeat} seats`;
    els.vehicleType.appendChild(opt);
  }
}

async function loadPaymentMethods() {
  const res = await fetch("/api/payment/methods");
  const methods = await asJson(res);
  els.paymentMethod.innerHTML = "";
  for (const m of methods) {
    const opt = document.createElement("option");
    opt.value = m.code;
    opt.textContent = m.displayName;
    els.paymentMethod.appendChild(opt);
  }
}

function estimatePayload() {
  return {
    pickUpLongitude: Number(els.pickUpLongitude.value),
    pickUpLatitude: Number(els.pickUpLatitude.value),
    dropOffLongitude: Number(els.dropOffLongitude.value),
    dropOffLatitude: Number(els.dropOffLatitude.value),
    vehicleTypeId: Number(els.vehicleType.value),
  };
}

async function estimate() {
  const p = estimatePayload();
  const q = new URLSearchParams({
    pickUpLongitude: String(p.pickUpLongitude),
    pickUpLatitude: String(p.pickUpLatitude),
    dropOffLongitude: String(p.dropOffLongitude),
    dropOffLatitude: String(p.dropOffLatitude),
    vehicleTypeId: String(p.vehicleTypeId),
  });
  const res = await fetch(`/api/bookings/estimate?${q.toString()}`);
  const data = await asJson(res);
  els.estimateBox.textContent = JSON.stringify(data, null, 2);
}

async function createBooking() {
  const p = estimatePayload();
  const body = {
    ...p,
    paymentMethod: els.paymentMethod.value,
  };

  const res = await fetch("/api/bookings", {
    method: "POST",
    headers: headers(),
    body: JSON.stringify(body),
  });

  const data = await asJson(res);
  els.createBox.textContent = JSON.stringify(data, null, 2);
  currentBookingId = String(data.bookingId);
  broadcastToDriver(currentBookingId);
  startPolling();
}

function broadcastToDriver(bookingId) {
  const payload = {
    bookingId,
    at: new Date().toISOString(),
    note: "New booking created by client",
  };
  localStorage.setItem("trangsm_latest_booking", JSON.stringify(payload));
}

async function fetchBooking() {
  if (!currentBookingId) return;
  const res = await fetch(`/api/bookings/${currentBookingId}`);
  const data = await asJson(res);
  els.bookingBox.textContent = JSON.stringify(data, null, 2);

  if (data.checkoutUrl) {
    els.checkoutWrap.classList.remove("hidden");
    els.checkoutLink.href = data.checkoutUrl;
    els.checkoutLink.textContent = "Open Checkout URL";
  }
}

function startPolling() {
  if (pollTimer) clearInterval(pollTimer);
  fetchBooking().catch((e) => (els.bookingBox.textContent = String(e)));
  pollTimer = setInterval(() => {
    fetchBooking().catch((e) => (els.bookingBox.textContent = String(e)));
  }, 3000);
}

async function init() {
  try {
    await Promise.all([loadVehicleTypes(), loadPaymentMethods()]);
    els.estimateBtn.addEventListener("click", () =>
      estimate().catch((e) => (els.estimateBox.textContent = String(e)))
    );
    els.createBtn.addEventListener("click", () =>
      createBooking().catch((e) => (els.createBox.textContent = String(e)))
    );
  } catch (e) {
    els.estimateBox.textContent = `Init failed: ${String(e)}`;
  }
}

init();


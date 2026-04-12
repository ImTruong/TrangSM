const els = {
  driverId: document.getElementById("driverId"),
  driverRoles: document.getElementById("driverRoles"),
  watchBookingId: document.getElementById("watchBookingId"),
  watchBtn: document.getElementById("watchBtn"),
  driverLog: document.getElementById("driverLog"),
  bookingState: document.getElementById("bookingState"),
  acceptBtn: document.getElementById("acceptBtn"),
  rejectBtn: document.getElementById("rejectBtn"),
  startBtn: document.getElementById("startBtn"),
  completeBtn: document.getElementById("completeBtn"),
};

let activeBookingId = null;
let pollTimer = null;

function log(message) {
  const line = `[${new Date().toLocaleTimeString()}] ${message}`;
  const prev = els.driverLog.textContent || "";
  els.driverLog.textContent = `${line}\n${prev}`;
}

function asJson(res) {
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

function headers() {
  return {
    "Content-Type": "application/json",
    "X-User-Id": String(els.driverId.value || ""),
    "X-User-Roles": String(els.driverRoles.value || ""),
  };
}

async function fetchBooking() {
  if (!activeBookingId) return;
  const res = await fetch(`/api/bookings/${activeBookingId}`);
  const data = await asJson(res);
  els.bookingState.textContent = JSON.stringify(data, null, 2);
  if (data.status === "FINDING_DRIVER" && !data.driverId) {
    notify(`Booking ${data.bookingId} is ready for driver action`);
  }
}

function startPolling() {
  if (pollTimer) clearInterval(pollTimer);
  fetchBooking().catch((e) => log(String(e)));
  pollTimer = setInterval(() => fetchBooking().catch((e) => log(String(e))), 3000);
}

async function action(pathSuffix) {
  if (!activeBookingId) throw new Error("No active booking id");
  const driverId = Number(els.driverId.value);
  if (!Number.isInteger(driverId) || driverId <= 0) {
    throw new Error("Invalid driver id");
  }
  const payload = { driverId };
  const res = await fetch(`/api/bookings/${activeBookingId}/${pathSuffix}`, {
    method: "POST",
    headers: headers(),
    body: JSON.stringify(payload),
  });
  const data = await asJson(res);
  els.bookingState.textContent = JSON.stringify(data, null, 2);
  log(`Action ${pathSuffix} success for booking ${activeBookingId}`);
}

function notify(message) {
  log(message);
  if ("Notification" in window) {
    if (Notification.permission === "granted") {
      new Notification("Driver Notification", { body: message });
    } else if (Notification.permission !== "denied") {
      Notification.requestPermission();
    }
  }
}

function setActiveBooking(id) {
  activeBookingId = String(id).trim();
  if (!activeBookingId) return;
  els.watchBookingId.value = activeBookingId;
  log(`Watching booking ${activeBookingId}`);
  startPolling();
}

window.addEventListener("storage", (ev) => {
  if (ev.key !== "trangsm_latest_booking" || !ev.newValue) return;
  try {
    const payload = JSON.parse(ev.newValue);
    if (payload.bookingId) {
      notify(`New booking signal received: ${payload.bookingId}`);
      setActiveBooking(payload.bookingId);
    }
  } catch {
    // Ignore malformed payload.
  }
});

function init() {
  els.watchBtn.addEventListener("click", () => {
    const id = String(els.watchBookingId.value || "").trim();
    if (!id) return;
    setActiveBooking(id);
  });

  els.acceptBtn.addEventListener("click", () => action("accept").catch((e) => log(String(e))));
  els.rejectBtn.addEventListener("click", () => action("reject").catch((e) => log(String(e))));
  els.startBtn.addEventListener("click", () => action("start").catch((e) => log(String(e))));
  els.completeBtn.addEventListener("click", () => action("complete").catch((e) => log(String(e))));

  const latest = localStorage.getItem("trangsm_latest_booking");
  if (latest) {
    try {
      const payload = JSON.parse(latest);
      if (payload.bookingId) setActiveBooking(payload.bookingId);
    } catch {
      // Ignore malformed cache.
    }
  }
}

init();


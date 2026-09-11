# CarService (reference)

**What:** System service hosting vehicle, audio, power, user, UX restriction policies.

**Why:** Apps must not open `/dev` or CAN. One privileged process talks to VHAL.

**Process:** `com.android.car` (not this APK).

**Binder:** App `Car` client → CarService stub → property HAL client.

**Crash:** Apps receive disconnect; properties become unavailable; reconnect is required.

**Boot:** Started as a core service after native HALs register with hwservicemanager / servicemanager (AIDL).

**This project:** no CarService module. Logs will never show `dumpsys car_service` entries for `FakeVehicleRepository`.

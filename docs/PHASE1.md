# Phase 1 — Compose dashboard + fake repository

Phase **1b** added Maps & Navigation. Vehicle data is still fake. See [13-navigation.md](13-navigation.md).


Stopped here on purpose. Do not add Car API, Car Service, VHAL, AIDL, or C++ until Phase 1 is running on a device/emulator and the interview questions below are answered.

---

## Honest architecture (today)

```text
┌─────────────────────────────────────────────┐
│  Process: com.mycar.dashboard  (this APK)   │
│                                             │
│  Compose UI  →  ViewModel  →  Fake repo     │
│                      ▲              │       │
│                      └── StateFlow ─┘       │
└─────────────────────────────────────────────┘

NOT in this APK (they exist on a real AAOS image, unused by us):
  com.android.car          → real Car Service
  vendor vehicle HAL       → real VHAL (native)
  android.hardware.automotive.vehicle  → AIDL HAL
```

If someone says “our app implements VHAL”, that is wrong. VHAL is a **vendor HAL** started by `init`, not an app class.

---

## What we built / why / where it runs

### 1. Dashboard application (Compose + MVVM)

1. **What:** Landscape “MY CAR” UI: speed, fuel, temp, gear, door, RPM, Accelerate, Brake.
2. **Why:** You need a visible contract before wiring framework APIs. Large type and 76dp buttons match AAOS glanceability, not phone Material defaults.
3. **Process:** `com.mycar.dashboard`.
4. **Language:** Kotlin + Jetpack Compose.
5. **Level:** Application.
6. **Data to next layer:** Click → ViewModel function → `VehicleRepository`.
7. **Internally:** `MainActivity` gets the repository from `DashboardApplication`, `viewModel()` scopes `DashboardViewModel`, `collectAsState()` recomposes.
8. **Verify:** Run on AVD, tap Accelerate, speed jumps by 5.
9. **What can go wrong:** Forgetting `application` name in the manifest → ClassCastException. Portrait phone still works but is cramped; we lock landscape.
10. **Interview angle:** Why MVVM instead of calling a service from the Composable.

### 2. `VehicleRepository` interface

1. **What:** App-level API: `vehicleState`, `accelerate()`, `brake()`.
2. **Why:** Swap Fake → CarPropertyManager later without rewriting UI.
3. **Process:** App process.
4. **Language:** Kotlin.
5. **Level:** Application (this interface is **not** the Car API).
6. **Next layer:** Implementation (fake today).
7. **Internally:** `StateFlow` is a hot stream of the latest snapshot — similar *idea* to property events, but it is **not** VHAL subscribe.
8. **Verify:** Unit test `FakeVehicleRepositoryTest`.
9. **What can go wrong:** Putting `android.car` types on this interface too early, then you cannot unit-test without the AAOS SDK.
10. **Interview angle:** Difference between an app repository and `CarPropertyManager`.

### 3. `FakeVehicleRepository` (MOCK / LEARNING LAYER)

1. **What:** In-memory vehicle numbers. Accelerate `+5` km/h, brake `-5` floored at 0, RPM from speed, fuel `-1` on accelerate, gear P/D.
2. **Why:** UI and logging work with zero privileges. You cannot talk to real VHAL from a normal Play-style app without the car permission + AAOS image.
3. **Process:** Same as the UI. **No Binder, no other process.**
4. **Language:** Kotlin.
5. **Level:** Application mock. **Not HAL-level.**
6. **Next layer:** There is none. StateFlow goes back to the ViewModel.
7. **Internally:** `MutableStateFlow.update { }` is atomic enough for a single-thread main-dispatcher UI. Not a substitute for a C++ mutex in VHAL.
8. **Verify:** Logcat `CAR_FAKE_REPO` after each tap; unit tests for 0→5→10→5→0→0.
9. **What can go wrong:** Treating this as “our VHAL”. It will not survive an interview.
10. **Interview angle:** Why apps must not talk to vehicle hardware / `/dev` / CAN directly.

### 4. Logging

Tags: `CAR_APP`, `CAR_FAKE_REPO`.

Example Accelerate tap:

```text
D/CAR_APP: Accelerate clicked
D/CAR_FAKE_REPO: ACCELERATE mock: speed 0 → 5 km/h, rpm 950, gear D
D/CAR_APP: UI state: speed=5 km/h rpm=950 fuel=99% gear=D
```

Later phases will insert `CAR_API`, `CAR_SERVICE`, `VHAL`, `VEHICLE_SIM` **between** click and UI state.

---

## AAOS emulator vs this APK

| Piece | On AAOS emulator | Used in Phase 1? |
| ----- | ---------------- | ---------------- |
| Our dashboard APK | You install it | Yes |
| Real Car Service | Already running as system | **No** |
| Real VHAL | Vendor/emulator image | **No** |
| `android.car` library | On the emulator classpath for signed/privileged apps; also as optional SDK extra | **No** (do not add the dependency yet) |

**Creating an Automotive AVD (Studio):**

1. SDK Manager → SDK Tools → enable **Android Automotive** system images if listed; or SDK Platforms → show package details → Automotive.
2. Device Manager → Create device → **Automotive** category (e.g. Automotive 1080p landscape).
3. Pick a system image such as **Android 13/14 Automotive with Google Play / Google APIs** (x86_64).
4. Cold boot. The car launcher is not a phone home screen.

If the Automotive image is missing, install via sdkmanager (example; API may differ on your SDK):

```text
sdkmanager --list | findstr automotive
```

AOSP-built emulator images are a **different path** (hours of source sync). Phase 1 does not need AOSP.

**Permissions we do not request yet:** `android.car.permission.CAR_SPEED`, `CONTROL_CAR_CLIMATE`, etc. Those are signature/privileged or system-only depending on property. A normal debug APK on a phone **cannot** read real speed from VHAL.

---

## Property list (names only — not real IDs yet)

| Dashboard field | Conceptual AAOS property (later) | Phase 1 |
| --------------- | -------------------------------- | ------- |
| Speed | `PERF_VEHICLE_SPEED` (standard) | Fake int km/h |
| Fuel | `FUEL_LEVEL` (standard) | Fake % |
| Temp | HVAC set-point (standard family) | Fake °C, display only |
| Gear | `GEAR_SELECTION` / `CURRENT_GEAR` (standard) | Fake enum |
| Door | `DOOR_LOCK` / door pos (standard, area IDs) | Fake CLOSED |
| RPM | `ENGINE_RPM` (standard) | Fake int |

We will **not invent** colliding numeric IDs. When we add `android.car`, we will copy IDs from `VehiclePropertyIds`.

---

## GET / SET / SUBSCRIBE — what you are seeing vs what AAOS does

| Concept | Phase 1 stand-in | Real AAOS |
| ------- | ---------------- | --------- |
| GET | Read `vehicleState.value` | `CarPropertyManager.getProperty` → Car Service → VHAL `get` |
| SET | `accelerate()` mutates memory | `setProperty` → VHAL `set` → ECU/simulator |
| SUBSCRIBE | Collect `StateFlow` | `registerCallback` → VHAL subscribe → property events |

Do not write “we implemented SUBSCRIBE” on a résumé for Phase 1. You implemented **UI observation**.

---

## Debugging Phase 1

```text
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb logcat -s CAR_APP:D CAR_FAKE_REPO:D
adb shell dumpsys activity activities | findstr mycar
```

`dumpsys car_service` exists **on AAOS**, not on a phone. In Phase 1 it will **not** mention our fake repository. That is expected.

Studio: Debug `app`, breakpoint in `DashboardViewModel.onAccelerate` and `FakeVehicleRepository.accelerate`.

---

## What would be required for the *real* stack (preview, not this phase)

- **AOSP source** + lunch an emulator/device target to **modify** VHAL.
- **System privileges / privileged module** or correct car permissions to use many properties.
- **SELinux** + **VINTF** (`manifest.xml` for `android.hardware.automotive.vehicle`) for a vendor HAL.
- **Native C++** VHAL implementation (`DefaultVehicleHal` in AOSP is the emulator default).

We will touch those only in later phases, and only with mock-vs-real labels.

---

## Phase 1 interview questions

Answer these in chat **in your own words**. Do not look up a canned paragraph first. After you send answers, they will be scored and corrected.

**Q1.** In Phase 1, which process holds the vehicle speed, and why is that different from production AAOS?

**Q2.** Why should the Compose screen depend on `VehicleRepository` instead of `FakeVehicleRepository`?

**Q3.** What is VHAL, and does this APK contain one?

**Q4.** A coworker says “StateFlow is the same as VHAL property subscribe.” What is fair about that, and what is wrong?

**Q5.** Why can’t a normal Android application open a CAN socket or talk to an ECU to get speed?

---

When Phase 1 runs on your emulator and those five answers are in, we start Phase 2 (a dedicated vehicle simulator still behind the same repository — still not Car Service).

# AutoCore Infotainment — Architecture

**Product:** AutoCore Infotainment (this repo still uses application id `com.mycar.dashboard` so Phase 1 installs keep working).

**This increment:** Phase 1 dashboard **plus** Maps & Navigation (simulated map/GPS). Car Service, VHAL, AIDL HAL, and CAN are **not** running inside this APK.

---

## Layer responsibilities

```text
┌─────────────────────────────────────────────┐
│  AAOS Applications  (this APK — Kotlin)     │
│  Dashboard | Maps | Media* | Vehicle* | …   │
│  * stub destinations this increment         │
└──────────────────────┬──────────────────────┘
                       │ app interfaces only
                       ↓
┌─────────────────────────────────────────────┐
│  App domain (repositories / use cases)      │
│  VehicleRepository | NavigationRepository   │
│  MapProvider | UxRestrictionsEvaluator      │
└──────────────────────┬──────────────────────┘
                       │
          ┌────────────┴────────────┐
          ↓                         ↓
┌──────────────────┐     ┌─────────────────────┐
│ MOCK (in-process)│     │ Real AAOS (later)   │
│ FakeVehicleRepo  │     │ CarPropertyManager  │
│ SimulatedMap     │     │ LocationManager/GNSS│
│ Simulated GPS    │     │ CarUxRestrictions   │
└──────────────────┘     └──────────┬──────────┘
                                    ↓
                         CarService (com.android.car)
                                    ↓ Binder/AIDL
                         Vehicle HAL (vendor native)
                                    ↓
                         ECU / CAN (vehicle / vendor sim)
```

| Layer | Owns | Does not own |
| ----- | ---- | ------------ |
| Compose UI | Layout, large targets, collecting StateFlow | Property IDs, Binder, GPS math |
| ViewModel | UI state, user intents | HAL, CAN, map-provider internals |
| Use case | One user goal (search, start nav) | Android framework types |
| Repository | Orchestration, mapping errors | Drawing pixels |
| MapProvider | Search, route, nav session API | Vehicle HVAC, CarService |
| VehicleRepository | Speed, gear, lamps, … | Routes and map tiles |
| Car API / CarService / VHAL | Production vehicle I/O | **Not in this APK** |

---

## What is real vs simulated (this increment)

| Piece | Status |
| ----- | ------ |
| Jetpack Compose, MVVM, coroutines, StateFlow | **Real Android APIs** |
| `android.hardware.type.automotive` (optional) | **Real manifest feature** |
| `android.car` / `CarPropertyManager` | **Not used** |
| CarService / VHAL / AIDL HAL | **Not in this process** |
| `LocationManager` / Fused Location / Google Maps | **Not used** |
| Map tiles / commercial routing | **Not used** |
| `MapProvider` + West Bengal road graph | **Simulated (replaceable)** |
| GPS lat/lng/speed/bearing | **Simulated**; playback speed follows **vehicle repository speed** |
| UX restrictions while driving | **App-level policy** mirroring AAOS `CarUxRestrictionsManager` ideas |
| Power states ON/SUSPEND/… | **Documented + in-process enum only** |

---

## Data flow — vehicle speed to dashboard

```text
User tap ACCELERATE
  → DashboardViewModel
  → VehicleRepository.accelerate()
  → FakeVehicleRepository (in-memory)     [SIMULATED]
  → StateFlow<VehicleSnapshot>
  → ViewModel / Compose
```

Production (not wired):

```text
CarPropertyManager.get/subscribe
  → Binder → CarService → VHAL get/subscribe
  → ECU/CAN
```

---

## Data flow — HVAC set (design; HVAC UI later)

```text
Compose → ViewModel → UseCase → Repository
  → CarPropertyManager.setProperty          [REAL AAOS later]
  → CarService → VHAL set → HVAC ECU
```

Reverse:

```text
HVAC ECU → CAN → VHAL event → CarService
  → CarPropertyManager callback → Flow → ViewModel → Compose
```

---

## Data flow — navigation (implemented)

```text
GPS simulator (route progress × vehicle speed)
  → NavigationRepository
  → NavigationState StateFlow
  → NavigationViewModel
  → Compose map canvas + guidance strip
```

Search / route:

```text
UI query → SearchPlacesUseCase → MapProvider.searchPlaces()
UI place  → CalculateRouteUseCase → MapProvider.calculateRoute()
UI Go     → StartNavigationUseCase → MapProvider.startNavigation()
```

Vehicle coupling (implemented, still mock vehicle):

```text
FakeVehicleRepository.speed/gear
  → UxRestrictionsEvaluator
  → typing/settings limited while DRIVING/REVERSE
  → SimulatedGps advances along polyline using speed (km/h)
```

---

## Process boundaries (honest)

Today **one process**: `com.mycar.dashboard`.

On a production AAOS image you would additionally have:

- **`com.android.car`** — CarService (Java/Kotlin system server style service)
- **`android.hardware.automotive.vehicle-service`** (name varies) — VHAL native
- App UIDs talking over **Binder**; permissions checked in the service before VHAL

If CarService dies, apps lose `Car` connection (`Car.CarServiceLifecycleListener` / death). If VHAL dies, CarService reports unavailable properties. If **this** app dies, the mock vehicle and mock GPS die with it — unlike a real bus.

---

## AOSP tree mapping (future integration)

| This repo | AOSP-style home |
| --------- | ---------------- |
| `app/` AutoCore UI | `packages/apps/AutoCore/` |
| Diagnostic AIDL + service (later) | `packages/services/Car/` or `packages/services/AutoCore/` |
| Vehicle HAL impl (later) | `hardware/interfaces/automotive/vehicle/` + `vendor/autocore/vhal/` |
| CAN/ECU sim (later) | `vendor/autocore/can-sim/` (never in app APK) |

`Android.bp` sketches live under `aosp/` as **reference only**. Gradle remains the build that Android Studio runs.

---

## Technology choices (Phase 1 + Maps)

- Kotlin, Compose, MVVM, use cases, constructor injection via `DashboardApplication`
- No Hilt yet (keeps the learning graph visible)
- No Google Maps SDK (replaceable `MapProvider`)
- JUnit4 + coroutines-test for routing, GPS math, UX, ViewModels

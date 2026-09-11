# AutoCore Infotainment

Android Automotive **learning / portfolio** project. It is an end-to-end **architecture** plus a working APK — not a claim that this APK *is* CarService or VHAL.

**Current increment:** Phase 1 dashboard + **Maps & Navigation** (simulated map provider and GPS).

Read [docs/01-architecture.md](docs/01-architecture.md) and [docs/13-navigation.md](docs/13-navigation.md). Phases: [docs/PHASES.md](docs/PHASES.md).

## What you can run today

| You see | What it actually is |
| -------- | ------------------- |
| Speed / RPM / fuel / gear | `FakeVehicleRepository` in the **app process** |
| Map, route, turn-by-turn | `SimulatedMapProvider` + Canvas (no Google Maps, no `LocationManager`) |
| GPS movement | Playback along Kolkata → Howrah → Dankuni → Bardhaman, **speed from the fake vehicle** |
| Search locked while moving | App `UxRestrictionsEvaluator` (not `CarUxRestrictionsManager`) |

## Run

1. Android Studio, SDK 35, open this folder.
2. AVD: Automotive landscape **or** a landscape tablet (`android.hardware.type.automotive` is `required="false"`).
3. Run `app`.

### Maps demo

1. Tab **MAPS**.
2. Search `Bardhaman` (only while parked).
3. Tap the place → **GO**.
4. **SPEED +** on the maps screen (or Home **ACCELERATE**). The car marker advances; search is disabled while driving.
5. **REROUTE** simulates off-route; **CANCEL** stops.

Logcat:

```text
adb logcat -s CAR_APP:D CAR_FAKE_REPO:D CAR_NAV:D CAR_MAP:D CAR_GPS:D CAR_UX:D CAR_POWER:D
```

Unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

## Module layout (this APK)

```text
app/
  ui/shell          AutoCore landscape shell
  ui/dashboard      Digital cockpit
  navigation/       MapProvider, sim GPS, routing, Compose map
  data/fake         Vehicle mock
  ux/               Driving distraction policy
  power/            In-process power enum
  vehicle/          Property catalog (names, not AOSP IDs)
aosp/               Android.bp sketches only
docs/               Architecture + interview notes
```

## Not in this APK (and not pretended)

- `android.car` / `CarPropertyManager` / CarService
- Vehicle HAL (HIDL or AIDL)
- CAN / ECU C++ on the vehicle bus
- Google Maps SDK / Directions API
- Media3, Bluetooth, HVAC write path (tabs are placeholders)

Those are documented so the next phases can plug in behind the same interfaces (`VehicleRepository`, `MapProvider`).

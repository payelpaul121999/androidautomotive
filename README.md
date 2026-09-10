# MY CAR — AAOS learning project

Phase **1 of 7**. Working Compose dashboard on a fake in-process vehicle repository.

This is **not** Car Service, **not** VHAL, and **not** `android.car`. Those come in later phases. Read [docs/PHASE1.md](docs/PHASE1.md) before changing architecture.

## Run (Android Studio)

1. Install **Android Studio** (Koala / Ladybug or newer) with SDK 35.
2. File → Open → this folder (`jsonview` / project root).
3. Create an emulator (either is valid for Phase 1):
   - **Preferred:** Automotive system image (API 33/34, x86_64) via Device Manager → Automotive.
   - **OK for UI only:** a landscape phone/tablet AVD, because `android.hardware.type.automotive` is `required="false"` in Phase 1.
4. Run the `app` configuration.

## Verify

- Speed starts at `0 km/h`, gear `P`, RPM `0`, fuel `100%`.
- **ACCELERATE** → speed `+5`, gear `D`, RPM rises, fuel drops 1%.
- **BRAKE** at `0` stays `0` (never negative).
- Logcat filter: `CAR_APP CAR_FAKE_REPO`

```bash
adb logcat -s CAR_APP:D CAR_FAKE_REPO:D
```

Unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

On Windows PowerShell, if the wrapper JAR is missing, open the project in Android Studio once (it generates the wrapper) or run:

```bash
gradle wrapper
```

## What comes next (do not implement yet)

| Phase | What |
| ----- | ---- |
| 2 | Dedicated vehicle simulator (still mock, richer physics) |
| 3 | Real Car API / `CarPropertyManager` on AAOS |
| 4 | Talk about real Car Service + VHAL (AOSP / privileged) |
| 5 | GET / SET / SUBSCRIBE against real properties |
| 6 | C++, Binder, AIDL where they actually live |
| 7 | CAN / fake ECU behind VHAL |

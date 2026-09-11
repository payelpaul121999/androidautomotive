# Android Automotive Framework (reference)

These are **real AAOS APIs**. This APK does **not** call them yet.

## Car API

Apps obtain `Car` (`Car.createCar`) then managers. Requires the right `android.car.permission.*` and usually an automotive image.

| Manager | Role |
| ------- | ---- |
| `CarPropertyManager` | GET / SET / subscribe vehicle properties |
| `CarAudioManager` | Cabin audio zones, volume groups |
| `CarPowerManager` | Vehicle power / shutdown / garage mode |
| `CarUxRestrictionsManager` | Distraction optimization while driving |
| `CarUserManager` | Multi-user (driver / passenger / guest) |

## Process

- App: your UID / APK
- CarService: `com.android.car`
- Connection: Binder

## Permissions (examples of real names)

- `android.car.permission.CAR_SPEED` — speed (typically privileged)
- `android.car.permission.CONTROL_CAR_CLIMATE` — HVAC write
- `android.car.permission.CAR_POWER` — power callbacks

A Play-style debug APK on a phone **cannot** read VHAL speed. Do not claim otherwise.

## What we emulate in-app

`UxRestrictionsEvaluator` copies the *policy idea* of `CarUxRestrictionsManager` (no typing while moving). It is **not** the framework manager.

# Debugging

```text
adb logcat -s CAR_APP:D CAR_FAKE_REPO:D CAR_NAV:D CAR_MAP:D CAR_GPS:D CAR_UX:D CAR_POWER:D
```

Breakpoints: `SimulatedGpsProvider`, `DefaultNavigationRepository`, `NavigationViewModel`, `UxRestrictionsEvaluator`.

`dumpsys location` will **not** show our simulator. `dumpsys car_service` will **not** list our fake properties.

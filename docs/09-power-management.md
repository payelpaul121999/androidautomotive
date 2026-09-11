# Power management

## Production

```text
Vehicle power controller
  → VHAL power properties / AP power state
  → CarPowerManager
  → CarService notifies apps
  → Application saves state, stops camera/sensors, prepares shutdown
```

States commonly discussed: ON, SHUTDOWN_PREPARE, SUSPEND, WAIT_FOR_VHAL, etc. (see `CarPowerManager` on the platform you target).

## This increment

`VehiclePowerController` is an **in-process** enum (`ON`, `SUSPEND`, `RESUME`, `SHUTDOWN`) so the navigation module can pause GPS playback on SUSPEND. It is **not** `CarPowerManager`.

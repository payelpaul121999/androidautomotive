# AIDL and Binder (reference + future diagnostic service)

## Binder in one paragraph

A client in process A holds a `Binder` proxy. Calls marshal parcels to process B’s stub. The kernel binder driver copies data and wakes the server thread pool. Death recipients run if B dies.

```text
Application UID
  → Binder transaction
  → Service onTransact / stub
  → permission check (enforceCallingPermission)
  → business logic
```

## Custom diagnostic API (not implemented this increment)

Planned: `IVehicleDiagnostic.aidl` — `getVehicleHealth`, DTCs, `getECUStatus`.

That service would be a **custom** project API, not an AOSP Vehicle HAL replacement. It would still sit *above* VHAL or a simulator, with signature/privileged permission — never an unrestricted Binder.

# Development phases

| Phase | Deliverable | Status |
| ----- | ----------- | ------ |
| 1 | Compose dashboard + `VehicleRepository` fake | Done |
| 1b | Maps & Navigation + `MapProvider` sim + UX policy | **This increment** |
| 2 | Richer vehicle simulator (physics, more properties) | Next |
| 3 | Optional `CarPropertyManager` adapter on AAOS image | Later |
| 4 | Document/run against real CarService on emulator | Later |
| 5 | GET/SET/SUBSCRIBE mapping to `VehiclePropertyIds` | Later |
| 6 | AIDL diagnostic service (app-defined) | Later |
| 7 | Host C++ CAN/ECU → VHAL sketch | Later |

Do not skip to “we implemented VHAL” until phase 7 exists as a **native** binary outside the UI APK.

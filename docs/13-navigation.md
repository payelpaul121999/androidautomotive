# Maps & Navigation

## Abstraction

```text
Navigation UI (Compose canvas)
  → NavigationViewModel
  → Use cases
  → NavigationRepository
  → MapProvider          ← swap SimulatedMapProvider for Google/HERE later
  → SimulatedGpsProvider ← swap Android LocationManager later
```

`MapProvider` must not import Compose, `android.car`, or a Maps SDK.

## Simulated road network

Predefined nodes (approximate WGS84):

| Node | Lat | Lng |
| ---- | --- | --- |
| Kolkata (Park St) | 22.5535 | 88.3516 |
| Howrah | 22.5958 | 88.2636 |
| Dankuni | 22.6747 | 88.2984 |
| Bardhaman | 23.2324 | 87.8615 |

Routing is graph + polyline interpolation, **not** a commercial directions API.

## GPS playback

Advance along the active route using **vehicle speed** from `VehicleRepository` (still fake). Parked (0 km/h) → marker holds. Accelerate → marker moves. Bearing from segment heading.

## Offline

Interfaces: `MapDataCache` (empty impl). Online tiles / local MBTiles are future work. Search/routing already run fully offline because they are in-memory.

## Automotive UX

While `DrivingMode.DRIVING` or `REVERSE`, search `TextField` is disabled (`typingLimited`). Navigation guidance remains enabled.

## Events

`NAVIGATION_STARTED`, `NAVIGATION_STOPPED`, `DESTINATION_REACHED`, `ROUTE_CHANGED`, `REROUTING`, `GPS_LOST`, `GPS_RESTORED`, `OFF_ROUTE`.

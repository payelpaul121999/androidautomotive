# Interview notes (this increment)

## HVAC temperature 22 → 24 (production answer; HVAC not shipped yet)

Compose → ViewModel → UseCase → Repository → `CarPropertyManager.setProperty` → Binder → CarService → VHAL `set` → HVAC ECU (CAN). Event comes back subscribe → Flow → UI.

Today there is **no** HVAC write path; cabin temp is a fake field on the vehicle snapshot.

## User starts navigation to Bardhaman (implemented)

1. Compose search (only if parked / typing allowed)
2. `SearchPlacesUseCase` → `SimulatedMapProvider.searchPlaces`
3. `CalculateRouteUseCase` walks the West Bengal graph
4. `StartNavigationUseCase` sets route; GPS ticker uses vehicle speed
5. Canvas projects lat/lng; strip shows next maneuver, distance, ETA
6. `UxRestrictionsEvaluator` blocks typing once gear is D and speed > 0

## Crash questions

| Dies | Effect today | Production |
| ---- | ------------ | ---------- |
| App | Mock GPS and vehicle gone | CarService/VHAL keep running |
| CarService | N/A | `Car` disconnect |
| VHAL | N/A | Properties unavailable |

## Questions to practice

1. Why is `MapProvider` an interface?
2. Why must routing not live in a Composable?
3. How will you replace simulated GPS with `LocationManager` without rewriting the map canvas?
4. What is the difference between app UX rules and `CarUxRestrictionsManager`?
5. Which process owns `PERF_VEHICLE_SPEED` on a real AAOS device?

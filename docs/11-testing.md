# Testing

| Layer | This increment |
| ----- | -------------- |
| Unit | Fake vehicle, West Bengal router, GPS interpolation, UX evaluator, navigation VM |
| Integration | Not VHAL yet; repository + map provider in-process |
| UI | Manual on landscape AVD; Compose previews |
| Failure | MapProvider errors as `VehicleResult`; GPS lost event simulated |

```bash
./gradlew :app:testDebugUnitTest
```

# HIDL vs AIDL (HAL)

| | HIDL (legacy) | AIDL HAL (current AAOS) |
| - | ------------- | ----------------------- |
| IDL | `.hal` | `.aidl` |
| Runtime | hwservicemanager | binder / servicemanager |
| Vehicle | `android.hardware.automotive.vehicle@2.0` | `android.hardware.automotive.vehicle` AIDL |

New work should target **AIDL VHAL**. This app does not implement either.

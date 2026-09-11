# AOSP placement (reference only)

Gradle builds the APK. These `Android.bp` files show where code would land in a vendor tree. They are **not** compiled by Android Studio.

```text
packages/apps/AutoCore/          ← app UI
packages/services/AutoCore/      ← future diagnostic service
hardware/interfaces/automotive/  ← AOSP VHAL AIDL (do not fork blindly)
vendor/autocore/vhal/            ← vendor VHAL impl
vendor/autocore/can-sim/         ← ECU/CAN simulator
```

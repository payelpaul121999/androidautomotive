# Vehicle HAL (reference)

**What:** Vendor HAL exposing vehicle properties (AIDL `android.hardware.automotive.vehicle` on modern AAOS).

**Why:** Stable contract between Android and the vehicle network.

**Process:** Native vendor service, not an Activity.

**GET/SET/SUBSCRIBE:** Implemented in VHAL; CarService is the Java facade.

**This project:** no C++ VHAL binary in the APK. A later phase may add a **host-side** simulator under `vendor/` sketches — still not production silicon.

package com.mycar.dashboard.data.fake

import com.mycar.dashboard.data.Gear
import com.mycar.dashboard.data.VehicleRepository
import com.mycar.dashboard.data.VehicleUiState
import com.mycar.dashboard.logging.CarLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max
import kotlin.math.min

/**
 * =============================================================================
 * MOCK / LEARNING LAYER — Phase 1 only
 * =============================================================================
 *
 * This is NOT:
 *  - Android Automotive Car Service (`com.android.car`)
 *  - VHAL (Vehicle HAL, native vendor process)
 *  - android.car.Car / CarPropertyManager
 *  - AIDL Vehicle HAL (android.hardware.automotive.vehicle)
 *
 * This IS:
 *  - an in-process Kotlin object that holds fake numbers so the dashboard UI
 *    can be designed and clicked without AAOS privileges or AOSP.
 *
 * Process: same as the Dashboard APK
 * Language: Kotlin
 * Level: application
 *
 * Data movement today:
 *   Compose button → ViewModel → FakeVehicleRepository → StateFlow → Compose
 *
 * Real AAOS data movement (Phase 3+):
 *   App → Car API → Car Service → Binder → VHAL → vehicle / simulator
 *
 * Migration path:
 *  1. Keep [VehicleRepository] as the ViewModel dependency.
 *  2. Add a `CarPropertyVehicleRepository` that uses Car.createCar() and
 *     CarPropertyManager.
 *  3. Delete or disable this class once GET/SET/SUBSCRIBE work on an AAOS image.
 */
class FakeVehicleRepository : VehicleRepository {

    private val _vehicleState = MutableStateFlow(VehicleUiState())
    override val vehicleState: StateFlow<VehicleUiState> = _vehicleState.asStateFlow()

    override fun accelerate() {
        _vehicleState.update { current ->
            val nextSpeed = min(MAX_SPEED_KMH, current.speedKmh + SPEED_STEP_KMH)
            val next = current.copy(
                speedKmh = nextSpeed,
                rpm = rpmForSpeed(nextSpeed),
                gear = if (nextSpeed > 0) Gear.D else Gear.P,
                fuelPercent = if (nextSpeed > current.speedKmh) {
                    max(0, current.fuelPercent - FUEL_DROP)
                } else {
                    current.fuelPercent
                },
            )
            CarLog.fakeRepo(
                "ACCELERATE mock: speed ${current.speedKmh} → ${next.speedKmh} km/h, " +
                    "rpm ${next.rpm}, gear ${next.gear}",
            )
            next
        }
    }

    override fun brake() {
        _vehicleState.update { current ->
            val nextSpeed = max(0, current.speedKmh - SPEED_STEP_KMH)
            val next = current.copy(
                speedKmh = nextSpeed,
                rpm = rpmForSpeed(nextSpeed),
                gear = if (nextSpeed == 0) Gear.P else Gear.D,
            )
            CarLog.fakeRepo(
                "BRAKE mock: speed ${current.speedKmh} → ${next.speedKmh} km/h, rpm ${next.rpm}",
            )
            next
        }
    }

    private fun rpmForSpeed(speedKmh: Int): Int {
        if (speedKmh <= 0) return 0
        return min(MAX_RPM, IDLE_RPM + speedKmh * RPM_PER_KMH)
    }

    private companion object {
        const val SPEED_STEP_KMH = 5
        const val MAX_SPEED_KMH = 180
        const val IDLE_RPM = 800
        const val RPM_PER_KMH = 30
        const val MAX_RPM = 6500
        const val FUEL_DROP = 1
    }
}

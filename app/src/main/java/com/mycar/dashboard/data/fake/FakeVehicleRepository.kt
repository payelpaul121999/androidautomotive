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
 * MOCK / LEARNING LAYER — not CarService, not VHAL, not android.car.
 * Same process as the UI.
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
                parkingBrake = nextSpeed == 0,
                fuelPercent = if (nextSpeed > current.speedKmh) {
                    max(0, current.fuelPercent - FUEL_DROP)
                } else {
                    current.fuelPercent
                },
                rangeKm = max(0, (current.fuelPercent - if (nextSpeed > current.speedKmh) FUEL_DROP else 0) * 6),
                mediaTitle = "Drive mix",
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
                parkingBrake = nextSpeed == 0,
                mediaTitle = if (nextSpeed == 0) "Parked — no media" else current.mediaTitle,
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

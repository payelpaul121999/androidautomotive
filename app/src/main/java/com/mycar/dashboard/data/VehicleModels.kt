package com.mycar.dashboard.data

import kotlinx.coroutines.flow.StateFlow

/**
 * Snapshot the dashboard and other features can render.
 *
 * Fields map conceptually to AAOS properties. Phase 1/1b does not use real IDs.
 */
data class VehicleUiState(
    val speedKmh: Int = 0,
    val fuelPercent: Int = 100,
    val batteryPercent: Int = 82,
    val engineTempC: Int = 90,
    val outsideTempC: Int = 24,
    val cabinTempC: Int = 24,
    val odometerKm: Int = 12480,
    val rangeKm: Int = 420,
    val gear: Gear = Gear.P,
    val parkingBrake: Boolean = true,
    val seatbeltFastened: Boolean = true,
    val doorClosed: Boolean = true,
    val lightsOn: Boolean = false,
    val rpm: Int = 0,
    val mediaTitle: String = "Parked — no media",
)

enum class Gear {
    P,
    R,
    N,
    D,
}

/**
 * Application-facing vehicle contract.
 *
 * Today: [com.mycar.dashboard.data.fake.FakeVehicleRepository]
 * Later: CarPropertyManager adapter.
 *
 * Keep UI off android.car types so unit tests run on the JVM.
 */
interface VehicleRepository {
    val vehicleState: StateFlow<VehicleUiState>
    fun accelerate()
    fun brake()
}

package com.mycar.dashboard.data

/**
 * Vehicle state the dashboard can render.
 *
 * These fields map conceptually to AAOS VehicleProperty IDs. Phase 1 does not
 * use the real property IDs — we only show names the UI understands.
 * Phase 3+ will map these to [android.car.VehiclePropertyIds].
 */
data class VehicleUiState(
    val speedKmh: Int = 0,
    val fuelPercent: Int = 100,
    val cabinTempC: Int = 24,
    val gear: Gear = Gear.P,
    val doorClosed: Boolean = true,
    val rpm: Int = 0,
)

enum class Gear {
    P,
    R,
    N,
    D,
}

/**
 * Application-facing contract.
 *
 * Today: implemented by [com.mycar.dashboard.data.fake.FakeVehicleRepository]
 * Later: implemented by a Car API / CarPropertyManager adapter.
 *
 * Keep UI and ViewModel talking only to this interface so we can swap the
 * backend without rewriting Compose.
 */
interface VehicleRepository {
    val vehicleState: kotlinx.coroutines.flow.StateFlow<VehicleUiState>
    fun accelerate()
    fun brake()
}

package com.mycar.dashboard.vehicle

/**
 * Conceptual property list for interviews. Numeric IDs will come from
 * [android.car.VehiclePropertyIds] in a later phase — we do not invent colliding IDs.
 *
 * Access:
 *  - READ_ONLY vs WRITABLE
 *  - Change mode: CONTINUOUS (stream), ON_CHANGE, STATIC
 */
enum class VehiclePropertyAccess { READ_ONLY, WRITABLE }

enum class VehiclePropertyChangeMode { CONTINUOUS, ON_CHANGE, STATIC }

data class VehiclePropertySpec(
    val name: String,
    val access: VehiclePropertyAccess,
    val changeMode: VehiclePropertyChangeMode,
    val aospHint: String,
)

object VehiclePropertyCatalog {
    val all: List<VehiclePropertySpec> = listOf(
        spec("VEHICLE_SPEED", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.CONTINUOUS, "PERF_VEHICLE_SPEED"),
        spec("ENGINE_RPM", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.CONTINUOUS, "ENGINE_RPM"),
        spec("FUEL_LEVEL", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "FUEL_LEVEL"),
        spec("BATTERY_LEVEL", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "EV_BATTERY_LEVEL / INFO_EV_BATTERY"),
        spec("ENGINE_TEMPERATURE", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.CONTINUOUS, "ENGINE_COOLANT_TEMP"),
        spec("OUTSIDE_TEMPERATURE", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "ENV_OUTSIDE_TEMPERATURE"),
        spec("ODOMETER", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "PERF_ODOMETER"),
        spec("RANGE", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "RANGE_REMAINING (vendor or computed)"),
        spec("GEAR_POSITION", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "GEAR_SELECTION / CURRENT_GEAR"),
        spec("PARKING_BRAKE", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "PARKING_BRAKE_ON"),
        spec("SEATBELT_STATUS", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "SEAT_BELT_BUCKLED"),
        spec("DOOR_STATUS", VehiclePropertyAccess.READ_ONLY, VehiclePropertyChangeMode.ON_CHANGE, "DOOR_POS / DOOR_LOCK"),
        spec("LIGHT_STATUS", VehiclePropertyAccess.WRITABLE, VehiclePropertyChangeMode.ON_CHANGE, "HEADLIGHTS_STATE / LIGHTS_*"),
        spec("HVAC_TEMPERATURE", VehiclePropertyAccess.WRITABLE, VehiclePropertyChangeMode.ON_CHANGE, "HVAC_TEMPERATURE_SET"),
        spec("HVAC_POWER", VehiclePropertyAccess.WRITABLE, VehiclePropertyChangeMode.ON_CHANGE, "HVAC_POWER_ON"),
        spec("HVAC_FAN_SPEED", VehiclePropertyAccess.WRITABLE, VehiclePropertyChangeMode.ON_CHANGE, "HVAC_FAN_SPEED"),
        spec("AC_MODE", VehiclePropertyAccess.WRITABLE, VehiclePropertyChangeMode.ON_CHANGE, "HVAC_AC_ON"),
    )

    private fun spec(
        name: String,
        access: VehiclePropertyAccess,
        mode: VehiclePropertyChangeMode,
        aospHint: String,
    ) = VehiclePropertySpec(name, access, mode, aospHint)
}

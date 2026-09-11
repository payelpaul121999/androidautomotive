package com.mycar.dashboard.core.result

/**
 * App-level result type. This is NOT a VHAL status code.
 * Later adapters map CarPropertyManager / Binder failures into [VehicleErrorCode].
 */
sealed interface VehicleResult<out T> {
    data class Success<T>(val data: T) : VehicleResult<T>
    data class Error(
        val code: VehicleErrorCode,
        val message: String,
    ) : VehicleResult<Nothing>
}

enum class VehicleErrorCode {
    ECU_UNAVAILABLE,
    VHAL_UNAVAILABLE,
    BINDER_FAILURE,
    AIDL_SERVICE_DEAD,
    INVALID_PROPERTY,
    NETWORK_UNAVAILABLE,
    CAN_CORRUPT,
    TIMEOUT,
    PERMISSION_DENIED,
    UNSUPPORTED_PROPERTY,
    GPS_UNAVAILABLE,
    ROUTE_NOT_FOUND,
    OFF_ROUTE,
    POWER_SUSPENDED,
}

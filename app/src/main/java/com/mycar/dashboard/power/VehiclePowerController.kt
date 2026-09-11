package com.mycar.dashboard.power

import com.mycar.dashboard.logging.CarLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class VehiclePowerState { ON, SUSPEND, RESUME, SHUTDOWN }

/**
 * In-process stand-in for CarPowerManager callbacks.
 * Not connected to VHAL AP_POWER_STATE.
 */
class VehiclePowerController {

    private val _state = MutableStateFlow(VehiclePowerState.ON)
    val state: StateFlow<VehiclePowerState> = _state.asStateFlow()

    fun enter(next: VehiclePowerState) {
        CarLog.power("${_state.value} → $next (simulated CarPowerManager)")
        _state.value = when (next) {
            VehiclePowerState.RESUME -> VehiclePowerState.ON
            else -> next
        }
    }

    fun isInteractive(): Boolean =
        _state.value == VehiclePowerState.ON
}

package com.mycar.dashboard.ux

import com.mycar.dashboard.data.Gear
import com.mycar.dashboard.data.VehicleUiState
import com.mycar.dashboard.logging.CarLog

enum class DrivingMode { PARKED, DRIVING, REVERSE }

data class UxRestrictions(
    val mode: DrivingMode,
    val navigationEnabled: Boolean,
    val mediaLimited: Boolean,
    val videoDisabled: Boolean,
    val settingsLimited: Boolean,
    val typingLimited: Boolean,
    val reason: String,
)

/**
 * App-level distraction policy. Production should observe
 * CarUxRestrictionsManager — this class does not call AAOS.
 */
object UxRestrictionsEvaluator {

    fun evaluate(vehicle: VehicleUiState): UxRestrictions {
        val mode = when {
            vehicle.gear == Gear.R -> DrivingMode.REVERSE
            vehicle.speedKmh > 0 || vehicle.gear == Gear.D -> DrivingMode.DRIVING
            else -> DrivingMode.PARKED
        }
        val restrictions = when (mode) {
            DrivingMode.PARKED -> UxRestrictions(
                mode = mode,
                navigationEnabled = true,
                mediaLimited = false,
                videoDisabled = false,
                settingsLimited = false,
                typingLimited = false,
                reason = "Parked — full input allowed",
            )
            DrivingMode.DRIVING -> UxRestrictions(
                mode = mode,
                navigationEnabled = true,
                mediaLimited = true,
                videoDisabled = true,
                settingsLimited = true,
                typingLimited = true,
                reason = "Driving — typing and video disabled",
            )
            DrivingMode.REVERSE -> UxRestrictions(
                mode = mode,
                navigationEnabled = true,
                mediaLimited = true,
                videoDisabled = true,
                settingsLimited = true,
                typingLimited = true,
                reason = "Reverse — keep eyes on path",
            )
        }
        CarLog.ux("mode=${restrictions.mode} typingLimited=${restrictions.typingLimited}")
        return restrictions
    }
}

package com.mycar.dashboard.ux

import com.mycar.dashboard.data.Gear
import com.mycar.dashboard.data.VehicleUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UxRestrictionsEvaluatorTest {

    @Test
    fun parked_allowsTyping() {
        val ux = UxRestrictionsEvaluator.evaluate(VehicleUiState())
        assertEquals(DrivingMode.PARKED, ux.mode)
        assertFalse(ux.typingLimited)
        assertTrue(ux.navigationEnabled)
    }

    @Test
    fun driving_blocksTypingAndVideo() {
        val ux = UxRestrictionsEvaluator.evaluate(
            VehicleUiState(speedKmh = 40, gear = Gear.D, parkingBrake = false),
        )
        assertEquals(DrivingMode.DRIVING, ux.mode)
        assertTrue(ux.typingLimited)
        assertTrue(ux.videoDisabled)
        assertTrue(ux.navigationEnabled)
    }

    @Test
    fun reverse_blocksTyping() {
        val ux = UxRestrictionsEvaluator.evaluate(VehicleUiState(gear = Gear.R, speedKmh = 5))
        assertEquals(DrivingMode.REVERSE, ux.mode)
        assertTrue(ux.typingLimited)
    }
}

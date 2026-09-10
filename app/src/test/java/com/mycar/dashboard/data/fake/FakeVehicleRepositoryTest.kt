package com.mycar.dashboard.data.fake

import com.mycar.dashboard.data.Gear
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeVehicleRepositoryTest {

    @Test
    fun accelerateThenBrake_neverGoesNegative() {
        val repo = FakeVehicleRepository()
        assertEquals(0, repo.vehicleState.value.speedKmh)
        assertEquals(Gear.P, repo.vehicleState.value.gear)

        repo.accelerate()
        assertEquals(5, repo.vehicleState.value.speedKmh)
        repo.accelerate()
        assertEquals(10, repo.vehicleState.value.speedKmh)

        repo.brake()
        assertEquals(5, repo.vehicleState.value.speedKmh)
        repo.brake()
        assertEquals(0, repo.vehicleState.value.speedKmh)
        repo.brake()
        assertEquals(0, repo.vehicleState.value.speedKmh)
        assertEquals(Gear.P, repo.vehicleState.value.gear)
    }

    @Test
    fun accelerate_updatesRpmAndDropsFuel() {
        val repo = FakeVehicleRepository()
        val startFuel = repo.vehicleState.value.fuelPercent
        repo.accelerate()
        assertTrue(repo.vehicleState.value.rpm > 0)
        assertEquals(Gear.D, repo.vehicleState.value.gear)
        assertEquals(startFuel - 1, repo.vehicleState.value.fuelPercent)
    }
}

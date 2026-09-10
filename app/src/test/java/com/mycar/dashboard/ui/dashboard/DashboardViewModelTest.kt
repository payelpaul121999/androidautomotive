package com.mycar.dashboard.ui.dashboard

import com.mycar.dashboard.data.fake.FakeVehicleRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardViewModelTest {

    @Test
    fun accelerateUpdatesViewModelSpeed() {
        val repo = FakeVehicleRepository()
        val vm = DashboardViewModel(repo)

        assertEquals(0, vm.uiState.speedKmh)
        vm.onAccelerate()
        assertEquals(5, vm.uiState.speedKmh)
        assertEquals(1, vm.tapCount)
        vm.onAccelerate()
        assertEquals(10, vm.uiState.speedKmh)
        vm.onBrake()
        assertEquals(5, vm.uiState.speedKmh)
    }
}

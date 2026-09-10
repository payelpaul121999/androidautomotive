package com.mycar.dashboard.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mycar.dashboard.data.VehicleRepository
import com.mycar.dashboard.data.VehicleUiState
import com.mycar.dashboard.logging.CarLog

class DashboardViewModel(
    private val repository: VehicleRepository,
) : ViewModel() {

    var uiState by mutableStateOf(repository.vehicleState.value)
        private set

    var tapCount by mutableIntStateOf(0)
        private set

    var lastAction by mutableStateOf("Ready — tap ACCELERATE")
        private set

    fun onAccelerate() {
        tapCount += 1
        CarLog.app("Accelerate clicked tap=$tapCount")
        repository.accelerate()
        uiState = repository.vehicleState.value
        lastAction = "ACCELERATE → ${uiState.speedKmh} km/h"
        CarLog.app("Speed updated = ${uiState.speedKmh} km/h")
    }

    fun onBrake() {
        tapCount += 1
        CarLog.app("Brake clicked tap=$tapCount")
        repository.brake()
        uiState = repository.vehicleState.value
        lastAction = "BRAKE → ${uiState.speedKmh} km/h"
        CarLog.app("Speed updated = ${uiState.speedKmh} km/h")
    }

    companion object {
        fun factory(repository: VehicleRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(repository) as T
                }
            }
    }
}

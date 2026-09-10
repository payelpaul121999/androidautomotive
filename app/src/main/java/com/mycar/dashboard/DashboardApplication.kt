package com.mycar.dashboard

import android.app.Application
import com.mycar.dashboard.data.VehicleRepository
import com.mycar.dashboard.data.fake.FakeVehicleRepository
import com.mycar.dashboard.logging.CarLog

/**
 * Application process entry.
 *
 * Layer: application
 * Process: com.mycar.dashboard (this APK)
 * Language: Kotlin
 *
 * MOCK / LEARNING LAYER: [FakeVehicleRepository] is constructed here so every
 * screen shares one in-memory vehicle. This is NOT Car Service and NOT VHAL.
 * On a real AAOS device, Car Service already runs in another process
 * (`com.android.car`) before any app starts.
 */
class DashboardApplication : Application() {

    lateinit var vehicleRepository: VehicleRepository
        private set

    override fun onCreate() {
        super.onCreate()
        CarLog.app("Application onCreate — Phase 1, mock repository only")
        vehicleRepository = FakeVehicleRepository()
    }
}

package com.mycar.dashboard

import android.app.Application
import com.mycar.dashboard.data.VehicleRepository
import com.mycar.dashboard.data.fake.FakeVehicleRepository
import com.mycar.dashboard.logging.CarLog
import com.mycar.dashboard.navigation.data.DefaultNavigationRepository
import com.mycar.dashboard.navigation.data.SimulatedMapProvider
import com.mycar.dashboard.navigation.domain.NavigationRepository
import com.mycar.dashboard.navigation.domain.RerouteUseCase
import com.mycar.dashboard.navigation.domain.SearchPlacesUseCase
import com.mycar.dashboard.navigation.domain.SelectDestinationUseCase
import com.mycar.dashboard.navigation.domain.StartNavigationUseCase
import com.mycar.dashboard.navigation.domain.StopNavigationUseCase
import com.mycar.dashboard.power.VehiclePowerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Application process entry and manual DI graph.
 *
 * MOCK: FakeVehicleRepository + SimulatedMapProvider live here.
 * Real AAOS: CarService already exists in another process before this runs.
 */
class DashboardApplication : Application() {

    private val appJob = SupervisorJob()
    val appScope: CoroutineScope = CoroutineScope(appJob + Dispatchers.Default)

    lateinit var vehicleRepository: VehicleRepository
        private set
    lateinit var powerController: VehiclePowerController
        private set
    lateinit var mapProvider: SimulatedMapProvider
        private set
    lateinit var navigationRepository: NavigationRepository
        private set
    lateinit var searchPlaces: SearchPlacesUseCase
        private set
    lateinit var selectDestination: SelectDestinationUseCase
        private set
    lateinit var startNavigation: StartNavigationUseCase
        private set
    lateinit var stopNavigation: StopNavigationUseCase
        private set
    lateinit var reroute: RerouteUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        CarLog.app("Application onCreate — Phase 1b dashboard + simulated navigation")
        vehicleRepository = FakeVehicleRepository()
        powerController = VehiclePowerController()
        mapProvider = SimulatedMapProvider(
            speedKmh = { vehicleRepository.vehicleState.value.speedKmh },
        )
        val navRepo = DefaultNavigationRepository(
            mapProvider = mapProvider,
            vehicleRepository = vehicleRepository,
            power = powerController,
            scope = appScope,
        )
        navigationRepository = navRepo
        searchPlaces = SearchPlacesUseCase(navRepo)
        selectDestination = SelectDestinationUseCase(navRepo)
        startNavigation = StartNavigationUseCase(navRepo)
        stopNavigation = StopNavigationUseCase(navRepo)
        reroute = RerouteUseCase(navRepo)
    }

    override fun onTerminate() {
        appJob.cancel()
        super.onTerminate()
    }
}

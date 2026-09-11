package com.mycar.dashboard.navigation.data

import com.mycar.dashboard.core.result.VehicleResult
import com.mycar.dashboard.data.VehicleRepository
import com.mycar.dashboard.logging.CarLog
import com.mycar.dashboard.navigation.domain.NavigationEvent
import com.mycar.dashboard.navigation.domain.NavigationRepository
import com.mycar.dashboard.navigation.domain.NavigationState
import com.mycar.dashboard.navigation.domain.Place
import com.mycar.dashboard.navigation.domain.PlaceCategory
import com.mycar.dashboard.navigation.domain.SavedPlaces
import com.mycar.dashboard.power.VehiclePowerController
import com.mycar.dashboard.power.VehiclePowerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class DefaultNavigationRepository(
    private val mapProvider: SimulatedMapProvider,
    private val vehicleRepository: VehicleRepository,
    private val power: VehiclePowerController,
    private val scope: CoroutineScope,
) : NavigationRepository {

    private val _navigationState = MutableStateFlow(NavigationState())
    override val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private val _savedPlaces = MutableStateFlow(
        SavedPlaces(
            home = WestBengalRoadNetwork.home,
            work = WestBengalRoadNetwork.work,
            favorites = listOf(WestBengalRoadNetwork.victoria),
            recents = emptyList(),
        ),
    )
    override val savedPlaces: StateFlow<SavedPlaces> = _savedPlaces.asStateFlow()

    private val _events = MutableStateFlow<NavigationEvent?>(null)
    override val events: StateFlow<NavigationEvent?> = _events.asStateFlow()

    private var ticker: Job? = null
    private var pendingDestination: Place? = null

    init {
        _navigationState.value = NavigationState(
            currentLocation = WestBengalRoadNetwork.kolkata.place.location,
            gpsAvailable = true,
        )
        startTicker()
        scope.launch {
            power.state.collect { state ->
                if (state == VehiclePowerState.SUSPEND || state == VehiclePowerState.SHUTDOWN) {
                    CarLog.nav("power=$state — pausing navigation ticker semantics")
                }
            }
        }
    }

    override suspend fun searchPlaces(query: String): List<Place> {
        return when (val result = mapProvider.searchPlaces(query)) {
            is VehicleResult.Success -> result.data
            is VehicleResult.Error -> {
                CarLog.nav("search failed ${result.code} ${result.message}")
                emptyList()
            }
        }
    }

    override suspend fun selectDestination(place: Place) {
        val origin = _navigationState.value.currentLocation
            ?: WestBengalRoadNetwork.kolkata.place.location
        when (val result = mapProvider.calculateRoute(origin, place)) {
            is VehicleResult.Error -> {
                CarLog.nav("route failed ${result.code}")
                emit(NavigationEvent.RouteChanged)
            }
            is VehicleResult.Success -> {
                pendingDestination = place
                rememberRecent(place)
                _navigationState.update {
                    it.copy(
                        destination = place,
                        route = result.data,
                        distanceRemainingMeters = result.data.totalDistanceMeters,
                        eta = result.data.totalDuration,
                        isNavigating = false,
                        lastEvent = NavigationEvent.RouteChanged,
                    )
                }
                emit(NavigationEvent.RouteChanged)
                CarLog.nav("destination=${place.name} dist=${result.data.totalDistanceMeters.toInt()}m")
            }
        }
    }

    override fun startNavigation() {
        val route = _navigationState.value.route ?: return
        mapProvider.startNavigation(route)
        _navigationState.update { it.copy(isNavigating = true, lastEvent = NavigationEvent.NavigationStarted) }
        emit(NavigationEvent.NavigationStarted)
        CarLog.nav("NAVIGATION_STARTED → ${route.destination.name}")
    }

    override fun stopNavigation() {
        mapProvider.stopNavigation()
        _navigationState.update {
            it.copy(
                isNavigating = false,
                isRerouting = false,
                nextInstruction = null,
                lastEvent = NavigationEvent.NavigationStopped,
            )
        }
        emit(NavigationEvent.NavigationStopped)
        CarLog.nav("NAVIGATION_STOPPED")
    }

    override fun cancelRoute() {
        stopNavigation()
        pendingDestination = null
        _navigationState.update {
            it.copy(destination = null, route = null, distanceRemainingMeters = 0.0, eta = null)
        }
    }

    override fun reroute() {
        val destination = _navigationState.value.destination ?: return
        mapProvider.simulateOffRoute()
        emit(NavigationEvent.OffRoute)
        _navigationState.update { it.copy(isRerouting = true, lastEvent = NavigationEvent.Rerouting) }
        emit(NavigationEvent.Rerouting)
        scope.launch {
            selectDestination(destination)
            if (_navigationState.value.route != null) {
                mapProvider.startNavigation(_navigationState.value.route!!)
                _navigationState.update {
                    it.copy(isNavigating = true, isRerouting = false, lastEvent = NavigationEvent.RouteChanged)
                }
            }
        }
        CarLog.nav("REROUTING (simulated off-route)")
    }

    override fun saveFavorite(place: Place) {
        _savedPlaces.update { current ->
            if (current.favorites.any { it.id == place.id }) current
            else current.copy(favorites = current.favorites + place.copy(category = PlaceCategory.FAVORITE))
        }
    }

    override fun setHome(place: Place) {
        _savedPlaces.update { it.copy(home = place.copy(category = PlaceCategory.HOME)) }
    }

    override fun setWork(place: Place) {
        _savedPlaces.update { it.copy(work = place.copy(category = PlaceCategory.WORK)) }
    }

    override fun simulateGpsLost(lost: Boolean) {
        mapProvider.simulateGpsLost(lost)
        _navigationState.update {
            it.copy(
                gpsAvailable = !lost,
                lastEvent = if (lost) NavigationEvent.GpsLost else NavigationEvent.GpsRestored,
            )
        }
        emit(if (lost) NavigationEvent.GpsLost else NavigationEvent.GpsRestored)
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = scope.launch {
            while (isActive) {
                delay(TICK_MS)
                if (!power.isInteractive()) continue
                if (!_navigationState.value.isNavigating) continue
                mapProvider.tick(TICK_MS / 1000.0)
                val snap = mapProvider.currentProgress() ?: continue
                val speed = vehicleRepository.vehicleState.value.speedKmh.coerceAtLeast(1)
                val etaSeconds = if (speed <= 0) {
                    snap.distanceRemainingMeters / 16.7
                } else {
                    snap.distanceRemainingMeters / (speed / 3.6)
                }
                _navigationState.update {
                    it.copy(
                        currentLocation = snap.location,
                        distanceRemainingMeters = snap.distanceRemainingMeters,
                        eta = etaSeconds.toInt().seconds,
                        nextInstruction = snap.instruction,
                        currentRoad = snap.currentRoad,
                    )
                }
                if (snap.arrived) {
                    CarLog.nav("DESTINATION_REACHED")
                    emit(NavigationEvent.DestinationReached)
                    stopNavigation()
                    _navigationState.update { it.copy(lastEvent = NavigationEvent.DestinationReached) }
                }
            }
        }
    }

    private fun rememberRecent(place: Place) {
        _savedPlaces.update { current ->
            val next = listOf(place) + current.recents.filterNot { it.id == place.id }
            current.copy(recents = next.take(8))
        }
    }

    private fun emit(event: NavigationEvent) {
        _events.value = event
    }

    private companion object {
        const val TICK_MS = 250L
    }
}

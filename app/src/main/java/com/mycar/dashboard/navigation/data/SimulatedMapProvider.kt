package com.mycar.dashboard.navigation.data

import com.mycar.dashboard.core.result.VehicleErrorCode
import com.mycar.dashboard.core.result.VehicleResult
import com.mycar.dashboard.logging.CarLog
import com.mycar.dashboard.navigation.domain.GeoLocation
import com.mycar.dashboard.navigation.domain.MapProvider
import com.mycar.dashboard.navigation.domain.Place
import com.mycar.dashboard.navigation.domain.Route
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.seconds

/**
 * Simulated map provider — no Google Maps, no network, no LocationManager.
 */
class SimulatedMapProvider(
    private val speedKmh: () -> Int,
    private val nowMillis: () -> Long = { System.currentTimeMillis() },
) : MapProvider {

    private val _location = MutableStateFlow(
        WestBengalRoadNetwork.kolkata.place.location.copy(timestampMillis = nowMillis()),
    )
    val location = _location.asStateFlow()

    private var activeRoute: Route? = null
    private var traveledMeters: Double = 0.0
    private var gpsLost: Boolean = false
    @Volatile
    var offRouteRequested: Boolean = false
        private set

    override suspend fun searchPlaces(query: String): VehicleResult<List<Place>> {
        val q = query.trim()
        if (q.isEmpty()) {
            return VehicleResult.Success(WestBengalRoadNetwork.catalogPlaces)
        }
        val hits = WestBengalRoadNetwork.catalogPlaces.filter {
            it.name.contains(q, ignoreCase = true) || it.address.contains(q, ignoreCase = true)
        }
        CarLog.map("search query='$q' hits=${hits.size}")
        return VehicleResult.Success(hits)
    }

    override suspend fun calculateRoute(origin: GeoLocation, destination: Place): VehicleResult<Route> {
        val from = WestBengalRoadNetwork.nearestNode(origin)
        val to = WestBengalRoadNetwork.nearestNode(destination.location)
        val segments = WestBengalRoadNetwork.shortestPath(from, to)
            ?: return VehicleResult.Error(VehicleErrorCode.ROUTE_NOT_FOUND, "No demo roads to ${destination.name}")
        if (segments.isEmpty()) {
            return VehicleResult.Error(VehicleErrorCode.ROUTE_NOT_FOUND, "Already at ${destination.name}")
        }
        val distance = segments.sumOf { it.distanceMeters }
        val speed = 16.7
        val route = Route(
            id = "rte-${from.id}-${to.id}",
            origin = origin,
            destination = destination,
            segments = segments,
            totalDistanceMeters = distance,
            totalDuration = (distance / speed).toInt().seconds,
        )
        CarLog.map("route ${from.id} → ${to.id} dist=${distance.toInt()} m")
        return VehicleResult.Success(route)
    }

    override fun observeLocation(): Flow<GeoLocation> = location

    override fun startNavigation(route: Route) {
        activeRoute = route
        traveledMeters = 0.0
        offRouteRequested = false
        CarLog.map("startNavigation ${route.destination.name}")
        publishProgress()
    }

    override fun stopNavigation() {
        CarLog.map("stopNavigation")
        activeRoute = null
        traveledMeters = 0.0
    }

    override fun simulateOffRoute() {
        offRouteRequested = true
        CarLog.map("simulateOffRoute")
    }

    override fun simulateGpsLost(lost: Boolean) {
        gpsLost = lost
        CarLog.gps(if (lost) "GPS_LOST (simulated)" else "GPS_RESTORED (simulated)")
    }

    fun isGpsLost(): Boolean = gpsLost

    fun tick(deltaSeconds: Double) {
        if (gpsLost) return
        val route = activeRoute ?: return
        val speedMps = speedKmh() / 3.6
        traveledMeters += speedMps * deltaSeconds
        publishProgress()
        if (traveledMeters >= route.totalDistanceMeters) {
            traveledMeters = route.totalDistanceMeters
        }
    }

    fun currentProgress() = activeRoute?.let {
        RouteProgressCalculator.progress(it, traveledMeters, speedKmh() / 3.6, nowMillis())
    }

    fun consumeOffRoute(): Boolean {
        val requested = offRouteRequested
        offRouteRequested = false
        return requested
    }

    private fun publishProgress() {
        val route = activeRoute ?: return
        val snap = RouteProgressCalculator.progress(route, traveledMeters, speedKmh() / 3.6, nowMillis())
        _location.value = snap.location
        CarLog.gps(
            "fix lat=${"%.5f".format(snap.location.latitude)} lng=${"%.5f".format(snap.location.longitude)} " +
                "speedKmh=${speedKmh()} remain=${snap.distanceRemainingMeters.toInt()}m",
        )
    }
}

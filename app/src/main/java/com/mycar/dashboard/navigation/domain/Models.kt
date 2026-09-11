package com.mycar.dashboard.navigation.domain

import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val speedMps: Double = 0.0,
    val bearing: Float = 0f,
    val altitude: Double = 8.0,
    val timestampMillis: Long = 0L,
)

enum class PlaceCategory { CITY, STATION, LANDMARK, HOME, WORK, FAVORITE, GENERIC }

data class Place(
    val id: String,
    val name: String,
    val address: String,
    val location: GeoLocation,
    val category: PlaceCategory = PlaceCategory.GENERIC,
)

enum class Maneuver {
    DEPART,
    STRAIGHT,
    TURN_LEFT,
    TURN_RIGHT,
    SLIGHT_LEFT,
    SLIGHT_RIGHT,
    ARRIVE,
}

data class RoadSegment(
    val id: String,
    val name: String,
    val fromNodeId: String,
    val toNodeId: String,
    val distanceMeters: Double,
    val maneuver: Maneuver,
    val polyline: List<GeoLocation>,
    val laneHint: String,
)

data class Route(
    val id: String,
    val origin: GeoLocation,
    val destination: Place,
    val segments: List<RoadSegment>,
    val totalDistanceMeters: Double,
    val totalDuration: Duration,
) {
    val polyline: List<GeoLocation> get() = segments.flatMap { it.polyline }
}

data class NavigationInstruction(
    val maneuver: Maneuver,
    val distanceMeters: Double,
    val roadName: String,
    val spokenText: String,
    val laneHint: String?,
)

data class NavigationState(
    val currentLocation: GeoLocation? = null,
    val destination: Place? = null,
    val route: Route? = null,
    val distanceRemainingMeters: Double = 0.0,
    val eta: Duration? = null,
    val nextInstruction: NavigationInstruction? = null,
    val currentRoad: String = "",
    val isNavigating: Boolean = false,
    val isRerouting: Boolean = false,
    val gpsAvailable: Boolean = true,
    val lastEvent: NavigationEvent? = null,
)

sealed interface NavigationEvent {
    data object NavigationStarted : NavigationEvent
    data object NavigationStopped : NavigationEvent
    data object DestinationReached : NavigationEvent
    data object RouteChanged : NavigationEvent
    data object Rerouting : NavigationEvent
    data object GpsLost : NavigationEvent
    data object GpsRestored : NavigationEvent
    data object OffRoute : NavigationEvent
}

data class SavedPlaces(
    val home: Place?,
    val work: Place?,
    val favorites: List<Place>,
    val recents: List<Place>,
)

fun Maneuver.arrow(): String = when (this) {
    Maneuver.DEPART -> "↑"
    Maneuver.STRAIGHT -> "↑"
    Maneuver.TURN_LEFT -> "↰"
    Maneuver.TURN_RIGHT -> "↱"
    Maneuver.SLIGHT_LEFT -> "↖"
    Maneuver.SLIGHT_RIGHT -> "↗"
    Maneuver.ARRIVE -> "◉"
}

fun formatDistance(meters: Double): String {
    if (meters < 1000) return "${meters.toInt()} m"
    return String.format(Locale.US, "%.1f km", meters / 1000.0)
}

fun formatEta(duration: Duration?): String {
    if (duration == null) return "--"
    val minutes = duration.inWholeSeconds.seconds.inWholeMinutes
    return if (minutes < 1) "<1 min" else "$minutes min"
}

package com.mycar.dashboard.navigation.data

import com.mycar.dashboard.navigation.domain.GeoLocation
import com.mycar.dashboard.navigation.domain.Maneuver
import com.mycar.dashboard.navigation.domain.NavigationInstruction
import com.mycar.dashboard.navigation.domain.Route
import kotlin.math.max

data class RouteProgress(
    val location: GeoLocation,
    val distanceRemainingMeters: Double,
    val traveledMeters: Double,
    val instruction: NavigationInstruction,
    val currentRoad: String,
    val arrived: Boolean,
)

/**
 * Pure geometry — unit-testable without Android.
 */
object RouteProgressCalculator {

    fun progress(route: Route, traveledMeters: Double, speedMps: Double, nowMillis: Long): RouteProgress {
        val total = route.totalDistanceMeters
        val remaining = max(0.0, total - traveledMeters)
        val arrived = traveledMeters >= total - 8.0
        val location = locationAt(route, traveledMeters).copy(
            speedMps = speedMps,
            timestampMillis = nowMillis,
        )
        val instruction = instructionAt(route, traveledMeters)
        val road = roadAt(route, traveledMeters)
        return RouteProgress(
            location = location,
            distanceRemainingMeters = remaining,
            traveledMeters = traveledMeters.coerceAtMost(total),
            instruction = instruction,
            currentRoad = road,
            arrived = arrived,
        )
    }

    fun locationAt(route: Route, traveledMeters: Double): GeoLocation {
        val points = route.polyline
        if (points.isEmpty()) return route.origin
        if (points.size == 1) return points.first()
        var remaining = traveledMeters
        for (i in 0 until points.lastIndex) {
            val a = points[i]
            val b = points[i + 1]
            val edge = WestBengalRoadNetwork.haversineMeters(a, b)
            if (remaining <= edge) {
                val t = if (edge == 0.0) 0.0 else remaining / edge
                return WestBengalRoadNetwork.interpolate(a, b, t)
            }
            remaining -= edge
        }
        return points.last()
    }

    fun instructionAt(route: Route, traveledMeters: Double): NavigationInstruction {
        var cursor = 0.0
        for (index in route.segments.indices) {
            val segment = route.segments[index]
            val end = cursor + segment.distanceMeters
            if (traveledMeters <= end || index == route.segments.lastIndex) {
                val toManeuver = (end - traveledMeters).coerceAtLeast(0.0)
                val nextManeuver = if (index == route.segments.lastIndex) {
                    Maneuver.ARRIVE
                } else {
                    route.segments[index + 1].maneuver
                }
                val nextRoad = if (index == route.segments.lastIndex) {
                    route.destination.name
                } else {
                    route.segments[index + 1].name
                }
                val spoken = when (nextManeuver) {
                    Maneuver.ARRIVE -> "Arrive at ${route.destination.name} in ${toManeuver.toInt()} m"
                    Maneuver.TURN_LEFT -> "Turn left in ${toManeuver.toInt()} m"
                    Maneuver.TURN_RIGHT -> "Turn right in ${toManeuver.toInt()} m"
                    Maneuver.SLIGHT_LEFT -> "Keep left in ${toManeuver.toInt()} m"
                    Maneuver.SLIGHT_RIGHT -> "Keep right in ${toManeuver.toInt()} m"
                    else -> "Continue on ${segment.name}"
                }
                return NavigationInstruction(
                    maneuver = if (traveledMeters < 40) Maneuver.DEPART else nextManeuver,
                    distanceMeters = toManeuver,
                    roadName = nextRoad,
                    spokenText = spoken,
                    laneHint = segment.laneHint,
                )
            }
            cursor = end
        }
        return NavigationInstruction(
            maneuver = Maneuver.ARRIVE,
            distanceMeters = 0.0,
            roadName = route.destination.name,
            spokenText = "You have arrived",
            laneHint = null,
        )
    }

    fun roadAt(route: Route, traveledMeters: Double): String {
        var cursor = 0.0
        for (segment in route.segments) {
            cursor += segment.distanceMeters
            if (traveledMeters <= cursor) return segment.name
        }
        return route.segments.lastOrNull()?.name.orEmpty()
    }
}

package com.mycar.dashboard.navigation.data

import com.mycar.dashboard.navigation.domain.Maneuver
import com.mycar.dashboard.navigation.domain.Place
import com.mycar.dashboard.navigation.domain.PlaceCategory
import com.mycar.dashboard.navigation.domain.Route
import kotlin.time.Duration.Companion.seconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteProgressCalculatorTest {

    private val route: Route = run {
        val dest = Place(
            id = "bardhaman",
            name = "Bardhaman",
            address = "demo",
            location = WestBengalRoadNetwork.bardhaman.place.location,
            category = PlaceCategory.CITY,
        )
        val segments = WestBengalRoadNetwork.shortestPath(
            WestBengalRoadNetwork.kolkata,
            WestBengalRoadNetwork.bardhaman,
        )!!
        Route(
            id = "test",
            origin = WestBengalRoadNetwork.kolkata.place.location,
            destination = dest,
            segments = segments,
            totalDistanceMeters = segments.sumOf { it.distanceMeters },
            totalDuration = 100.seconds,
        )
    }

    @Test
    fun start_isNearKolkata_andDepartInstruction() {
        val snap = RouteProgressCalculator.progress(route, 0.0, 0.0, 0L)
        assertEquals(22.5535, snap.location.latitude, 0.002)
        assertEquals(Maneuver.DEPART, snap.instruction.maneuver)
        assertTrue(snap.distanceRemainingMeters > 80_000)
        assertTrue(!snap.arrived)
    }

    @Test
    fun end_arrives() {
        val snap = RouteProgressCalculator.progress(route, route.totalDistanceMeters, 0.0, 0L)
        assertTrue(snap.arrived)
        assertEquals(0.0, snap.distanceRemainingMeters, 1.0)
    }
}

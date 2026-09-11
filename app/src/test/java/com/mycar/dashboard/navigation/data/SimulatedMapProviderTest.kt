package com.mycar.dashboard.navigation.data

import com.mycar.dashboard.core.result.VehicleResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulatedMapProviderTest {

    @Test
    fun search_bardhaman() = runBlocking {
        val provider = SimulatedMapProvider(speedKmh = { 0 })
        val result = provider.searchPlaces("bard")
        val places = (result as VehicleResult.Success).data
        assertTrue(places.any { it.name.contains("Bardhaman") })
    }

    @Test
    fun route_kolkataToHowrah() = runBlocking {
        val provider = SimulatedMapProvider(speedKmh = { 0 })
        val result = provider.calculateRoute(
            WestBengalRoadNetwork.kolkata.place.location,
            WestBengalRoadNetwork.howrah.place,
        )
        val route = (result as VehicleResult.Success).data
        assertTrue(route.segments.isNotEmpty())
        assertTrue(route.totalDistanceMeters > 1000)
    }
}

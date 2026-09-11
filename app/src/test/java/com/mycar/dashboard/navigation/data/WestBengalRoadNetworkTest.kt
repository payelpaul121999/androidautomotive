package com.mycar.dashboard.navigation.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WestBengalRoadNetworkTest {

    @Test
    fun kolkataToBardhaman_hasThreeHighwaySegments() {
        val from = WestBengalRoadNetwork.kolkata
        val to = WestBengalRoadNetwork.bardhaman
        val path = WestBengalRoadNetwork.shortestPath(from, to)!!
        assertEquals(3, path.size)
        assertEquals("howrah", path[0].toNodeId)
        assertEquals("dankuni", path[1].toNodeId)
        assertEquals("bardhaman", path[2].toNodeId)
        assertTrue(path.sumOf { it.distanceMeters } > 80_000)
    }

    @Test
    fun haversine_howrahIsWestOfKolkata() {
        val metres = WestBengalRoadNetwork.haversineMeters(
            WestBengalRoadNetwork.kolkata.place.location,
            WestBengalRoadNetwork.howrah.place.location,
        )
        assertTrue(metres in 8_000.0..20_000.0)
    }
}

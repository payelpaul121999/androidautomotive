package com.mycar.dashboard.navigation.domain

import com.mycar.dashboard.core.result.VehicleResult
import kotlinx.coroutines.flow.Flow

/**
 * Replaceable map backend. Do not import Compose, Maps SDK, or android.car here.
 *
 * Production swap: Google / HERE / offline graph implementing this interface.
 */
interface MapProvider {
    suspend fun searchPlaces(query: String): VehicleResult<List<Place>>
    suspend fun calculateRoute(origin: GeoLocation, destination: Place): VehicleResult<Route>
    fun observeLocation(): Flow<GeoLocation>
    fun startNavigation(route: Route)
    fun stopNavigation()
    fun simulateOffRoute()
    fun simulateGpsLost(lost: Boolean)
}

/**
 * Offline map data hook. Empty cache today; routing already works without network.
 */
interface MapDataCache {
    fun hasOfflinePack(regionId: String): Boolean
}

class InMemoryMapDataCache : MapDataCache {
    override fun hasOfflinePack(regionId: String): Boolean = regionId == "west-bengal-demo"
}

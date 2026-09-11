package com.mycar.dashboard.navigation.domain

import kotlinx.coroutines.flow.StateFlow

interface NavigationRepository {
    val navigationState: StateFlow<NavigationState>
    val savedPlaces: StateFlow<SavedPlaces>
    val events: StateFlow<NavigationEvent?>

    suspend fun searchPlaces(query: String): List<Place>
    suspend fun selectDestination(place: Place)
    fun startNavigation()
    fun stopNavigation()
    fun cancelRoute()
    fun reroute()
    fun saveFavorite(place: Place)
    fun setHome(place: Place)
    fun setWork(place: Place)
    fun simulateGpsLost(lost: Boolean)
}

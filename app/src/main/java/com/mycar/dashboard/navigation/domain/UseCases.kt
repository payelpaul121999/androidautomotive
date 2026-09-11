package com.mycar.dashboard.navigation.domain

class SearchPlacesUseCase(
    private val repository: NavigationRepository,
) {
    suspend operator fun invoke(query: String) = repository.searchPlaces(query)
}

class SelectDestinationUseCase(
    private val repository: NavigationRepository,
) {
    suspend operator fun invoke(place: Place) = repository.selectDestination(place)
}

class StartNavigationUseCase(
    private val repository: NavigationRepository,
) {
    operator fun invoke() = repository.startNavigation()
}

class StopNavigationUseCase(
    private val repository: NavigationRepository,
) {
    operator fun invoke() = repository.stopNavigation()
}

class RerouteUseCase(
    private val repository: NavigationRepository,
) {
    operator fun invoke() = repository.reroute()
}

package com.mycar.dashboard.navigation.ui

import com.mycar.dashboard.data.fake.FakeVehicleRepository
import com.mycar.dashboard.navigation.domain.NavigationEvent
import com.mycar.dashboard.navigation.domain.NavigationRepository
import com.mycar.dashboard.navigation.domain.NavigationState
import com.mycar.dashboard.navigation.domain.Place
import com.mycar.dashboard.navigation.domain.RerouteUseCase
import com.mycar.dashboard.navigation.domain.SavedPlaces
import com.mycar.dashboard.navigation.domain.SearchPlacesUseCase
import com.mycar.dashboard.navigation.domain.SelectDestinationUseCase
import com.mycar.dashboard.navigation.domain.StartNavigationUseCase
import com.mycar.dashboard.navigation.domain.StopNavigationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {

    private val main = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun typingIgnoredWhileDriving() {
        val vehicle = FakeVehicleRepository()
        vehicle.accelerate()
        val repo = RecordingNavRepo()
        val vm = NavigationViewModel(
            vehicleRepository = vehicle,
            searchPlaces = SearchPlacesUseCase(repo),
            selectDestination = SelectDestinationUseCase(repo),
            startNavigation = StartNavigationUseCase(repo),
            stopNavigation = StopNavigationUseCase(repo),
            reroute = RerouteUseCase(repo),
            navigationState = repo.navigationState,
            savedPlaces = repo.savedPlaces,
        )
        vm.onQueryChange("Howrah")
        assertEquals("", vm.query)
        assertEquals(0, repo.searchCalls)
    }

    private class RecordingNavRepo : NavigationRepository {
        var searchCalls = 0
        override val navigationState = MutableStateFlow(NavigationState())
        override val savedPlaces = MutableStateFlow(
            SavedPlaces(home = null, work = null, favorites = emptyList(), recents = emptyList()),
        )
        override val events = MutableStateFlow<NavigationEvent?>(null)
        override suspend fun searchPlaces(query: String): List<Place> {
            searchCalls += 1
            return emptyList()
        }
        override suspend fun selectDestination(place: Place) = Unit
        override fun startNavigation() = Unit
        override fun stopNavigation() = Unit
        override fun cancelRoute() = Unit
        override fun reroute() = Unit
        override fun saveFavorite(place: Place) = Unit
        override fun setHome(place: Place) = Unit
        override fun setWork(place: Place) = Unit
        override fun simulateGpsLost(lost: Boolean) = Unit
    }
}

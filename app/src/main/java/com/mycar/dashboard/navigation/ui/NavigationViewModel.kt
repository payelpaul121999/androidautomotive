package com.mycar.dashboard.navigation.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mycar.dashboard.data.VehicleRepository
import com.mycar.dashboard.logging.CarLog
import com.mycar.dashboard.navigation.domain.NavigationState
import com.mycar.dashboard.navigation.domain.Place
import com.mycar.dashboard.navigation.domain.RerouteUseCase
import com.mycar.dashboard.navigation.domain.SavedPlaces
import com.mycar.dashboard.navigation.domain.SearchPlacesUseCase
import com.mycar.dashboard.navigation.domain.SelectDestinationUseCase
import com.mycar.dashboard.navigation.domain.StartNavigationUseCase
import com.mycar.dashboard.navigation.domain.StopNavigationUseCase
import com.mycar.dashboard.ux.UxRestrictions
import com.mycar.dashboard.ux.UxRestrictionsEvaluator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NavigationViewModel(
    private val vehicleRepository: VehicleRepository,
    private val searchPlaces: SearchPlacesUseCase,
    private val selectDestination: SelectDestinationUseCase,
    private val startNavigation: StartNavigationUseCase,
    private val stopNavigation: StopNavigationUseCase,
    private val reroute: RerouteUseCase,
    navigationState: StateFlow<NavigationState>,
    savedPlaces: StateFlow<SavedPlaces>,
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<Place>>(emptyList())
        private set

    val ux: StateFlow<UxRestrictions> = vehicleRepository.vehicleState
        .map { UxRestrictionsEvaluator.evaluate(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            UxRestrictionsEvaluator.evaluate(vehicleRepository.vehicleState.value),
        )

    val nav: StateFlow<NavigationState> = navigationState
    val saved: StateFlow<SavedPlaces> = savedPlaces

    fun onQueryChange(value: String) {
        if (ux.value.typingLimited) {
            CarLog.nav("search blocked by UX restrictions")
            return
        }
        query = value
        viewModelScope.launch {
            searchResults = searchPlaces(value)
        }
    }

    fun onSelectPlace(place: Place) {
        viewModelScope.launch {
            CarLog.nav("UI selected ${place.name}")
            selectDestination(place)
        }
    }

    fun onStart() {
        CarLog.nav("UI start navigation")
        startNavigation()
    }

    fun onStop() {
        CarLog.nav("UI stop / cancel")
        stopNavigation()
    }

    fun onReroute() {
        reroute()
    }

    fun onHomeOrWork(place: Place?) {
        if (place == null) return
        onSelectPlace(place)
    }

    companion object {
        fun factory(
            vehicleRepository: VehicleRepository,
            searchPlaces: SearchPlacesUseCase,
            selectDestination: SelectDestinationUseCase,
            startNavigation: StartNavigationUseCase,
            stopNavigation: StopNavigationUseCase,
            reroute: RerouteUseCase,
            navigationState: StateFlow<NavigationState>,
            savedPlaces: StateFlow<SavedPlaces>,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NavigationViewModel(
                    vehicleRepository,
                    searchPlaces,
                    selectDestination,
                    startNavigation,
                    stopNavigation,
                    reroute,
                    navigationState,
                    savedPlaces,
                ) as T
            }
        }
    }
}

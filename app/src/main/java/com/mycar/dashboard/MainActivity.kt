package com.mycar.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mycar.dashboard.logging.CarLog
import com.mycar.dashboard.navigation.ui.NavigationViewModel
import com.mycar.dashboard.ui.dashboard.DashboardViewModel
import com.mycar.dashboard.ui.shell.AutoCoreShell
import com.mycar.dashboard.ui.theme.MyCarTheme

class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels {
        val app = application as DashboardApplication
        DashboardViewModel.factory(app.vehicleRepository)
    }

    private val navigationViewModel: NavigationViewModel by viewModels {
        val app = application as DashboardApplication
        NavigationViewModel.factory(
            vehicleRepository = app.vehicleRepository,
            searchPlaces = app.searchPlaces,
            selectDestination = app.selectDestination,
            startNavigation = app.startNavigation,
            stopNavigation = app.stopNavigation,
            reroute = app.reroute,
            navigationState = app.navigationRepository.navigationState,
            savedPlaces = app.navigationRepository.savedPlaces,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.isFocusableInTouchMode = true
        CarLog.app("MainActivity onCreate — AutoCore shell")
        setContent {
            MyCarTheme {
                AutoCoreShell(
                    dashboardViewModel = dashboardViewModel,
                    navigationViewModel = navigationViewModel,
                )
            }
        }
    }
}

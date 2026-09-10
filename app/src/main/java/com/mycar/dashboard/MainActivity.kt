package com.mycar.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mycar.dashboard.logging.CarLog
import com.mycar.dashboard.ui.dashboard.DashboardScreen
import com.mycar.dashboard.ui.dashboard.DashboardViewModel
import com.mycar.dashboard.ui.theme.MyCarTheme

class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels {
        val app = application as DashboardApplication
        DashboardViewModel.factory(app.vehicleRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CarLog.app("MainActivity onCreate — Compose dashboard")
        setContent {
            MyCarTheme {
                DashboardScreen(
                    state = dashboardViewModel.uiState,
                    tapCount = dashboardViewModel.tapCount,
                    lastAction = dashboardViewModel.lastAction,
                    onAccelerate = dashboardViewModel::onAccelerate,
                    onBrake = dashboardViewModel::onBrake,
                )
            }
        }
    }
}

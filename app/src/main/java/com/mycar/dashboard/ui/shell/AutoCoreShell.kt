package com.mycar.dashboard.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mycar.dashboard.navigation.ui.NavigationScreen
import com.mycar.dashboard.navigation.ui.NavigationViewModel
import com.mycar.dashboard.ui.dashboard.DashboardScreen
import com.mycar.dashboard.ui.dashboard.DashboardViewModel
import com.mycar.dashboard.ui.theme.MyCarColors
import com.mycar.dashboard.ux.dpadClickable

enum class AppTab { Home, Maps, Vehicle, Phone, Media }

@Composable
fun AutoCoreShell(
    dashboardViewModel: DashboardViewModel,
    navigationViewModel: NavigationViewModel,
) {
    var tab by rememberSaveable { mutableStateOf(AppTab.Home) }
    val nav by navigationViewModel.nav.collectAsStateWithLifecycle()
    val saved by navigationViewModel.saved.collectAsStateWithLifecycle()
    val ux by navigationViewModel.ux.collectAsStateWithLifecycle()
    val firstTabFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { firstTabFocus.requestFocus() }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (tab) {
                AppTab.Home -> DashboardScreen(
                    state = dashboardViewModel.uiState,
                    nav = nav,
                    tapCount = dashboardViewModel.tapCount,
                    lastAction = dashboardViewModel.lastAction,
                    onAccelerate = dashboardViewModel::onAccelerate,
                    onBrake = dashboardViewModel::onBrake,
                )
                AppTab.Maps -> NavigationScreen(
                    nav = nav,
                    saved = saved,
                    ux = ux,
                    query = navigationViewModel.query,
                    results = navigationViewModel.searchResults,
                    speedKmh = dashboardViewModel.uiState.speedKmh,
                    onQueryChange = navigationViewModel::onQueryChange,
                    onSelectPlace = navigationViewModel::onSelectPlace,
                    onStart = navigationViewModel::onStart,
                    onStop = navigationViewModel::onStop,
                    onReroute = navigationViewModel::onReroute,
                    onQuickPlace = navigationViewModel::onHomeOrWork,
                    onAccelerate = dashboardViewModel::onAccelerate,
                    onBrake = dashboardViewModel::onBrake,
                )
                AppTab.Vehicle -> PlaceholderPane("Vehicle", "HVAC and body controls land in a later phase.")
                AppTab.Phone -> PlaceholderPane("Phone", "Bluetooth / telephony later. Not a dialer mock of CarService.")
                AppTab.Media -> PlaceholderPane("Media", "Media3 session later. Separate from vehicle properties.")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppTab.entries.forEachIndexed { index, dest ->
                val selected = dest == tab
                Text(
                    text = dest.name.uppercase(),
                    color = if (selected) MyCarColors.amber else MyCarColors.muted,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .then(if (index == 0) Modifier.focusRequester(firstTabFocus) else Modifier)
                        .dpadClickable { tab = dest }
                        .padding(12.dp),
                )
            }
        }
    }
}

@Composable
private fun PlaceholderPane(title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, color = MyCarColors.amber, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(body, color = MyCarColors.muted, fontSize = 18.sp)
    }
}

package com.mycar.dashboard.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycar.dashboard.navigation.domain.NavigationState
import com.mycar.dashboard.navigation.domain.Place
import com.mycar.dashboard.navigation.domain.SavedPlaces
import com.mycar.dashboard.navigation.domain.arrow
import com.mycar.dashboard.navigation.domain.formatDistance
import com.mycar.dashboard.navigation.domain.formatEta
import com.mycar.dashboard.ui.theme.MyCarColors
import com.mycar.dashboard.ux.UxRestrictions

@Composable
fun NavigationScreen(
    nav: NavigationState,
    saved: SavedPlaces,
    ux: UxRestrictions,
    query: String,
    results: List<Place>,
    speedKmh: Int,
    onQueryChange: (String) -> Unit,
    onSelectPlace: (Place) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onReroute: () -> Unit,
    onQuickPlace: (Place?) -> Unit,
    onAccelerate: () -> Unit,
    onBrake: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1.35f)
                .fillMaxHeight(),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
            ) {
                SimulatedMapCanvas(
                    location = nav.currentLocation,
                    route = nav.route,
                )
                Text(
                    text = if (nav.gpsAvailable) "SIM MAP · GPS OK" else "SIM MAP · GPS LOST",
                    color = if (nav.gpsAvailable) MyCarColors.mint else MyCarColors.danger,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            GuidanceStrip(nav)
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("MAPS", color = MyCarColors.muted, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
            Text(
                text = ux.reason,
                color = if (ux.typingLimited) MyCarColors.danger else MyCarColors.mint,
                fontSize = 14.sp,
            )
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                enabled = !ux.typingLimited,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        if (ux.typingLimited) "Search disabled while driving" else "Search Kolkata, Howrah, Bardhaman…",
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickChip("Home") { onQuickPlace(saved.home) }
                QuickChip("Work") { onQuickPlace(saved.work) }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val list = results.ifEmpty { saved.recents + saved.favorites }
                items(list, key = { it.id }) { place ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            .clickable { onSelectPlace(place) }
                            .padding(12.dp),
                    ) {
                        Text(place.name, color = MyCarColors.amber, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        Text(place.address, color = MyCarColors.muted, fontSize = 14.sp)
                    }
                }
            }
            Text("Vehicle speed $speedKmh km/h (feeds simulated GPS)", color = MyCarColors.muted, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onAccelerate,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MyCarColors.amber),
                ) { Text("SPEED +") }
                Button(
                    onClick = onBrake,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MyCarColors.danger),
                ) { Text("SPEED −") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onStart,
                    enabled = nav.route != null && !nav.isNavigating,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MyCarColors.mint),
                ) { Text("GO", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onReroute,
                    enabled = nav.isNavigating,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MyCarColors.ice),
                ) { Text("REROUTE") }
                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MyCarColors.danger),
                ) { Text("CANCEL") }
            }
        }
    }
}

@Composable
fun GuidanceStrip(nav: NavigationState) {
    val instruction = nav.nextInstruction
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (instruction != null) {
                    "${instruction.maneuver.arrow()}  ${instruction.spokenText}"
                } else {
                    "Select a destination — demo roads: Kolkata → Howrah → Dankuni → Bardhaman"
                },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = listOfNotNull(
                    nav.currentRoad.takeIf { it.isNotBlank() },
                    instruction?.laneHint,
                ).joinToString("  ·  ").ifBlank { "Not navigating" },
                color = MyCarColors.muted,
                fontSize = 14.sp,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatDistance(nav.distanceRemainingMeters), color = MyCarColors.amber, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(formatEta(nav.eta), color = MyCarColors.ice, fontSize = 16.sp)
        }
    }
}

@Composable
private fun QuickChip(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        color = MyCarColors.ice,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

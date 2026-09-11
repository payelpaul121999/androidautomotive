package com.mycar.dashboard.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mycar.dashboard.data.Gear
import com.mycar.dashboard.data.VehicleUiState
import com.mycar.dashboard.navigation.domain.NavigationState
import com.mycar.dashboard.navigation.domain.arrow
import com.mycar.dashboard.navigation.ui.GuidanceStrip
import com.mycar.dashboard.ui.theme.MyCarColors
import com.mycar.dashboard.ui.theme.MyCarTheme
import com.mycar.dashboard.ux.DpadButton

@Composable
fun DashboardScreen(
    state: VehicleUiState,
    nav: NavigationState,
    tapCount: Int,
    lastAction: String,
    onAccelerate: () -> Unit,
    onBrake: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "AUTOCORE",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
            )
            Text(
                text = "${state.outsideTempC}°C",
                color = MyCarColors.ice,
                fontSize = 16.sp,
            )
            Text(
                text = "${state.speedKmh}",
                color = MyCarColors.amber,
                fontSize = 84.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
            Text("km/h", color = MyCarColors.muted, fontSize = 18.sp, letterSpacing = 3.sp)
            Text("RPM ${state.rpm}", color = MyCarColors.ice, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(lastAction, color = MyCarColors.mint, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text("taps: $tapCount", color = MyCarColors.muted, fontSize = 13.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                StatusRow("Fuel", "${state.fuelPercent}%", MyCarColors.mint)
                StatusRow("Battery", "${state.batteryPercent}%", MyCarColors.ice)
                StatusRow("Range", "${state.rangeKm} km", MyCarColors.amber)
                StatusRow("Engine", "${state.engineTempC}°C", MyCarColors.danger)
                StatusRow("Odo", "${state.odometerKm} km", MyCarColors.muted)
                StatusRow("Gear", "${state.gear.name}  ${if (state.parkingBrake) "P-BRAKE" else ""}".trim(), MyCarColors.amber)
                StatusRow("Doors", if (state.doorClosed) "CLOSED" else "OPEN", MyCarColors.mint)
                StatusRow("Belt", if (state.seatbeltFastened) "ON" else "OFF", MyCarColors.mint)
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("♪ ${state.mediaTitle}", color = MyCarColors.ice, fontSize = 14.sp, modifier = Modifier.weight(1f))
                val hint = nav.nextInstruction?.let { "${it.maneuver.arrow()} ${it.spokenText}" }
                    ?: "No active route"
                Text(hint, color = MyCarColors.amber, fontSize = 14.sp, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            GuidanceStrip(nav)
            Spacer(Modifier.height(10.dp))
            DpadButton(
                onClick = onAccelerate,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                containerColor = MyCarColors.amber,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) { Text("ACCELERATE", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            DpadButton(
                onClick = onBrake,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                containerColor = MyCarColors.danger,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) { Text("BRAKE", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MyCarColors.muted, fontSize = 16.sp)
        Text(
            value,
            color = valueColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Preview(device = "spec:width=1920dp,height=720dp,dpi=160")
@Composable
private fun DashboardPreview() {
    MyCarTheme {
        DashboardScreen(
            state = VehicleUiState(
                speedKmh = 72,
                fuelPercent = 68,
                cabinTempC = 24,
                gear = Gear.D,
                doorClosed = true,
                rpm = 2200,
                parkingBrake = false,
            ),
            nav = NavigationState(),
            tapCount = 3,
            lastAction = "ACCELERATE → 72 km/h",
            onAccelerate = {},
            onBrake = {},
        )
    }
}

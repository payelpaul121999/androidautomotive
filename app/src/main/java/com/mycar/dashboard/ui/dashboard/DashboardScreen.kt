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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.mycar.dashboard.ui.theme.MyCarColors
import com.mycar.dashboard.ui.theme.MyCarTheme

@Composable
fun DashboardScreen(
    state: VehicleUiState,
    tapCount: Int,
    lastAction: String,
    onAccelerate: () -> Unit,
    onBrake: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "MY CAR",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
            )
            Text(
                text = "${state.speedKmh}",
                color = MyCarColors.amber,
                fontSize = 88.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = "km/h",
                color = MyCarColors.muted,
                fontSize = 18.sp,
                letterSpacing = 3.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = lastAction,
                color = MyCarColors.mint,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "taps: $tapCount",
                color = MyCarColors.muted,
                fontSize = 14.sp,
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatusRow("Speed", "${state.speedKmh} km/h", MyCarColors.amber)
                StatusRow("Fuel", "${state.fuelPercent}%", MyCarColors.mint)
                StatusRow("Temp", "${state.cabinTempC}°C", MyCarColors.ice)
                StatusRow("Gear", state.gear.name, MyCarColors.amber)
                StatusRow("Door", if (state.doorClosed) "CLOSED" else "OPEN", MyCarColors.mint)
                StatusRow("RPM", "${state.rpm}", MyCarColors.ice)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onAccelerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyCarColors.amber,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("ACCELERATE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onBrake,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyCarColors.danger,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
            ) {
                Text("BRAKE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
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
        Text(label, color = MyCarColors.muted, fontSize = 18.sp)
        Text(
            value,
            color = valueColor,
            fontSize = 20.sp,
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
            ),
            tapCount = 3,
            lastAction = "ACCELERATE → 72 km/h",
            onAccelerate = {},
            onBrake = {},
        )
    }
}

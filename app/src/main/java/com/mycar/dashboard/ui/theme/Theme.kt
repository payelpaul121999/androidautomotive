package com.mycar.dashboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Panel = Color(0xFF121820)
private val Amber = Color(0xFFF5C16C)
private val Mint = Color(0xFF7EE0A8)
private val Ice = Color(0xFF9ECBFF)

private val Scheme = darkColorScheme(
    primary = Amber,
    onPrimary = Color(0xFF1A1204),
    secondary = Mint,
    background = Color(0xFF0B0F14),
    surface = Panel,
    onBackground = Color(0xFFE8EDF4),
    onSurface = Color(0xFFE8EDF4),
    onSurfaceVariant = Color(0xFF8B97A8),
)

@Composable
fun MyCarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme,
        content = content,
    )
}

object MyCarColors {
    val amber = Amber
    val mint = Mint
    val ice = Ice
    val danger = Color(0xFFFF7B8A)
    val muted = Color(0xFF8B97A8)
}

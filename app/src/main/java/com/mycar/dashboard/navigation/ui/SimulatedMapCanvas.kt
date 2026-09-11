package com.mycar.dashboard.navigation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.mycar.dashboard.navigation.domain.GeoLocation
import com.mycar.dashboard.navigation.domain.Route
import com.mycar.dashboard.ui.theme.MyCarColors

/**
 * Simulated map renderer. Not Google Maps. Projects WGS84 into the canvas.
 */
@Composable
fun SimulatedMapCanvas(
    location: GeoLocation?,
    route: Route?,
    modifier: Modifier = Modifier,
) {
    val points = route?.polyline.orEmpty()
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0E1620)),
    ) {
        val bounds = boundsOf(points, location)
        fun project(lat: Double, lng: Double): Offset {
            val x = ((lng - bounds.minLng) / bounds.lngSpan).toFloat() * size.width
            val y = (1f - ((lat - bounds.minLat) / bounds.latSpan).toFloat()) * size.height
            return Offset(x, y)
        }

        if (points.size >= 2) {
            val path = Path()
            val first = project(points.first().latitude, points.first().longitude)
            path.moveTo(first.x, first.y)
            points.drop(1).forEach {
                val p = project(it.latitude, it.longitude)
                path.lineTo(p.x, p.y)
            }
            drawPath(
                path = path,
                color = MyCarColors.ice.copy(alpha = 0.85f),
                style = Stroke(width = 10f, cap = StrokeCap.Round),
            )
        }

        location?.let { loc ->
            val car = project(loc.latitude, loc.longitude)
            rotate(degrees = loc.bearing, pivot = car) {
                val path = Path().apply {
                    moveTo(car.x, car.y - 22f)
                    lineTo(car.x - 14f, car.y + 18f)
                    lineTo(car.x + 14f, car.y + 18f)
                    close()
                }
                drawPath(path, MyCarColors.amber)
            }
        }

        route?.destination?.location?.let { dest ->
            val pin = project(dest.latitude, dest.longitude)
            drawCircle(MyCarColors.mint, radius = 14f, center = pin)
            drawCircle(Color(0xFF0E1620), radius = 6f, center = pin)
        }
    }
}

private data class GeoBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double,
) {
    val latSpan: Double get() = (maxLat - minLat).coerceAtLeast(0.02)
    val lngSpan: Double get() = (maxLng - minLng).coerceAtLeast(0.02)
}

private fun boundsOf(points: List<GeoLocation>, location: GeoLocation?): GeoBounds {
    val coords = buildList {
        addAll(points)
        location?.let { add(it) }
        if (isEmpty()) {
            add(GeoLocation(22.55, 88.35))
            add(GeoLocation(23.23, 87.86))
        }
    }
    val pad = 0.04
    return GeoBounds(
        minLat = coords.minOf { it.latitude } - pad,
        maxLat = coords.maxOf { it.latitude } + pad,
        minLng = coords.minOf { it.longitude } - pad,
        maxLng = coords.maxOf { it.longitude } + pad,
    )
}

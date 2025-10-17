package com.localify.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.lifecycle.onStart
import com.mapbox.maps.plugin.lifecycle.onStop

@Composable
fun RadiusSelectionScreen(
    selectedCity: String,
    radius: Double,
    onRadiusChanged: (Double) -> Unit
) {
    var radiusValue by remember { mutableStateOf(radius.toFloat()) }
    val context = LocalContext.current
    // Ithaca, NY as default center (replace with geocoded city center when available)
    val centerLat = 42.443961
    val centerLng = -76.501881

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {

        // Title
        Text(
            text = "Select Radius",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle with selected city
        Text(
            text = "Choose your search radius for $selectedCity",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mapbox map with overlayed radius UI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            // MapView
            var mapView: MapView? by remember { mutableStateOf(null) }
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    MapView(context).also { mv ->
                        mapView = mv
                        mv.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
                            mv.getMapboxMap().setCamera(
                                CameraOptions.Builder()
                                    .center(com.mapbox.geojson.Point.fromLngLat(centerLng, centerLat))
                                    .zoom(11.0)
                                    .build()
                            )
                        }
                        // Optional: lock gestures so the center remains fixed
                        mv.gestures.apply {
                            rotateEnabled = false
                            pitchEnabled = false
                            scrollEnabled = false
                            zoomEnabled = true
                        }
                    }
                },
                update = { /* no-op for now */ }
            )

            // Radius overlay and center marker drawn in Compose
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val maxRadius = minOf(size.width, size.height) / 2 * 0.8f
                val radiusPixels = (radiusValue / 50f) * maxRadius // visual scaling up to 50 miles

                // Ring
                drawCircle(
                    color = Color(0xFF007AFF),
                    radius = radiusPixels,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Fill
                drawCircle(
                    color = Color(0xFF007AFF).copy(alpha = 0.15f),
                    radius = radiusPixels,
                    center = Offset(centerX, centerY)
                )
                // Center marker
                drawCircle(
                    color = Color(0xFF007AFF),
                    radius = 8.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
            }

            // City label matching iOS style
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = selectedCity,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Radius slider
        Column {
            Slider(
                value = radiusValue,
                onValueChange = { 
                    radiusValue = it
                    onRadiusChanged(it.toDouble()) 
                },
                valueRange = 1f..50f,
                steps = 48,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF007AFF),
                    activeTrackColor = Color(0xFF007AFF),
                    inactiveTrackColor = Color.Gray
                )
            )

            Text(
                text = "Radius: ${'$'}{radiusValue.toInt()} miles",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Page indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (index == 1) Color(0xFF4A90E2) else Color.Gray,
                            RoundedCornerShape(4.dp)
                        )
                )
                if (index < 3) Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
// Note: For an accurate geodesic radius, next step is to render a polygon buffer (meters)
// as a FillLayer in Mapbox using the Annotation API or GeoJSON source.

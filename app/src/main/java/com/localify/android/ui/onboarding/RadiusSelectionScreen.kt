package com.localify.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.LaunchedEffect
// Google Maps (primary provider when key present)
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun RadiusSelectionScreen(
    selectedCity: String,
    radius: Double,
    onRadiusChanged: (Double) -> Unit
) {
    var radiusValue by remember { mutableStateOf(radius.toFloat()) }
    val context = LocalContext.current
    val lat = 42.443961
    val lng = -76.501881
    val mapCenterGms = remember { LatLng(lat, lng) }
    // Detect if Google Maps API key is present
    val hasGoogleMapsKey by remember {
        mutableStateOf(
            try {
                val ai = context.packageManager.getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
                val key = ai.metaData?.getString("com.google.android.geo.API_KEY")
                !key.isNullOrBlank()
            } catch (_: Throwable) { false }
        )
    }

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

        // Real Google Maps with nearby locations and city display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            var mapLoaded by remember { mutableStateOf(false) }
            
            if (hasGoogleMapsKey) {
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(mapCenterGms, 12f)
                }
                val uiSettings = remember {
                    MapUiSettings(
                        compassEnabled = false,
                        mapToolbarEnabled = false,
                        myLocationButtonEnabled = false,
                        rotationGesturesEnabled = false,
                        tiltGesturesEnabled = false,
                        scrollGesturesEnabled = false,
                        zoomGesturesEnabled = true,
                        zoomControlsEnabled = false
                    )
                }
                
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = uiSettings
                ) {
                    // Map loaded callback
                    MapEffect { googleMap ->
                        googleMap.setOnMapLoadedCallback { mapLoaded = true }
                    }
                    
                    // Center marker for the selected city
                    Marker(
                        state = MarkerState(position = mapCenterGms),
                        title = selectedCity,
                        snippet = "Selected location"
                    )
                    
                    // Sample nearby locations (you can replace with real data)
                    val nearbyLocations = remember {
                        listOf(
                            LatLng(lat + 0.01, lng + 0.01) to "Coffee Shop",
                            LatLng(lat - 0.008, lng + 0.015) to "Restaurant",
                            LatLng(lat + 0.005, lng - 0.012) to "Park",
                            LatLng(lat - 0.015, lng - 0.008) to "Shopping Center",
                            LatLng(lat + 0.012, lng + 0.008) to "Library",
                            LatLng(lat - 0.005, lng + 0.018) to "Hospital"
                        )
                    }
                    
                    // Add markers for nearby locations
                    nearbyLocations.forEach { (location, name) ->
                        Marker(
                            state = MarkerState(position = location),
                            title = name
                        )
                    }
                    
                    // Radius circle
                    Circle(
                        center = mapCenterGms,
                        radius = (radiusValue * 1609.34).toDouble(),
                        strokeColor = Color(0xFF007AFF),
                        strokeWidth = 3f,
                        fillColor = Color(0xFF007AFF).copy(alpha = 0.15f)
                    )
                }
            } else {
                // Fallback Canvas map when no Google API key
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // iOS-style map background
                    val bg = Color(0xFFF2F2F7)
                    drawRect(bg)
                    
                    // Park areas (green)
                    val parkColor = Color(0xFFC8E6C9)
                    drawRect(parkColor, Offset(size.width * 0.1f, size.height * 0.2f), androidx.compose.ui.geometry.Size(size.width * 0.25f, size.height * 0.15f))
                    drawRect(parkColor, Offset(size.width * 0.7f, size.height * 0.6f), androidx.compose.ui.geometry.Size(size.width * 0.2f, size.height * 0.25f))
                    
                    // Water bodies (blue)
                    val waterColor = Color(0xFF64B5F6)
                    drawRect(waterColor, Offset(size.width * 0.05f, size.height * 0.7f), androidx.compose.ui.geometry.Size(size.width * 0.3f, size.height * 0.1f))
                    
                    // Road network (white)
                    val roadColor = Color.White
                    val roadWidth = 4.dp.toPx()
                    // Main horizontal roads
                    drawLine(roadColor, Offset(0f, size.height * 0.3f), Offset(size.width, size.height * 0.3f), roadWidth)
                    drawLine(roadColor, Offset(0f, size.height * 0.6f), Offset(size.width, size.height * 0.6f), roadWidth)
                    // Main vertical roads
                    drawLine(roadColor, Offset(size.width * 0.4f, 0f), Offset(size.width * 0.4f, size.height), roadWidth)
                    drawLine(roadColor, Offset(size.width * 0.7f, 0f), Offset(size.width * 0.7f, size.height), roadWidth)
                    // Secondary roads
                    drawLine(roadColor, Offset(0f, size.height * 0.15f), Offset(size.width * 0.6f, size.height * 0.15f), 2.dp.toPx())
                    drawLine(roadColor, Offset(size.width * 0.2f, 0f), Offset(size.width * 0.2f, size.height * 0.8f), 2.dp.toPx())
                    
                    // Building blocks (light gray)
                    val buildingColor = Color(0xFFE0E0E0)
                    drawRect(buildingColor, Offset(size.width * 0.45f, size.height * 0.1f), androidx.compose.ui.geometry.Size(size.width * 0.15f, size.height * 0.15f))
                    drawRect(buildingColor, Offset(size.width * 0.15f, size.height * 0.35f), androidx.compose.ui.geometry.Size(size.width * 0.2f, size.height * 0.2f))
                    drawRect(buildingColor, Offset(size.width * 0.75f, size.height * 0.1f), androidx.compose.ui.geometry.Size(size.width * 0.2f, size.height * 0.4f))
                    
                    // Radius circle and center marker
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val maxRadius = minOf(size.width, size.height) / 2 * 0.8f
                    val radiusPixels = (radiusValue / 50f) * maxRadius
                    val blue = Color(0xFF007AFF)
                    
                    // Radius circle
                    drawCircle(color = blue.copy(alpha = 0.15f), radius = radiusPixels, center = Offset(centerX, centerY))
                    drawCircle(color = blue, radius = radiusPixels, center = Offset(centerX, centerY), style = Stroke(width = 3.dp.toPx()))
                    
                    // Center marker
                    drawCircle(color = blue, radius = 8.dp.toPx(), center = Offset(centerX, centerY))
                    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(centerX, centerY))
                }
                
                // Mark as loaded for Canvas fallback
                LaunchedEffect(Unit) {
                    mapLoaded = true
                }
            }
            
            // Loading indicator
            if (!mapLoaded) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Loading map…",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
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
                steps = 49,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF007AFF),
                    activeTrackColor = Color(0xFF007AFF),
                    inactiveTrackColor = Color.Gray
                )
            )

            Text(
                text = "Radius: ${radiusValue.toInt()} miles",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
// Note: Google Maps Compose Circle uses meters for radius.

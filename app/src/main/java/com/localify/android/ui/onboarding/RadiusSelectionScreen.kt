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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun RadiusSelectionScreen(
    selectedCity: String,
    radius: Double,
    onRadiusChanged: (Double) -> Unit
) {
    var radiusValue by remember { mutableStateOf(radius.toFloat()) }
    
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
        
        // iOS-style map interface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawIOSStyleMap(this, radiusValue)
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
                text = "Radius: ${radiusValue.toInt()} miles",
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

// Helper function to draw iOS-style map background
private fun drawIOSStyleMap(drawScope: DrawScope, radiusValue: Float) {
    with(drawScope) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = minOf(size.width, size.height) / 2 * 0.8f
        val radiusPixels = (radiusValue / 50f) * maxRadius // Scale radius (max 50 miles)
        
        // Fill background with iOS map color - more accurate to iOS Maps
        drawRect(
            color = Color(0xFFF0F0F0), // Lighter iOS map background
            size = androidx.compose.ui.geometry.Size(size.width, size.height)
        )
        
        // Add realistic map grid pattern
        val gridSpacing = 40.dp.toPx()
        val gridColor = Color(0xFFE0E0E0)
        val gridWidth = 1.dp.toPx()
        
        // Draw vertical grid lines
        var x = 0f
        while (x <= size.width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = gridWidth
            )
            x += gridSpacing
        }
        
        // Draw horizontal grid lines
        var y = 0f
        while (y <= size.height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = gridWidth
            )
            y += gridSpacing
        }
        
        // Add green areas (parks/nature) - more iOS-like colors
        drawRect(
            color = Color(0xFFB8E6B8), // iOS park green
            topLeft = Offset(centerX - 100f, centerY - 80f),
            size = androidx.compose.ui.geometry.Size(60f, 45f)
        )
        
        drawRect(
            color = Color(0xFFB8E6B8),
            topLeft = Offset(centerX + 50f, centerY + 30f),
            size = androidx.compose.ui.geometry.Size(80f, 55f)
        )
        
        // Add water body (iOS water blue)
        val waterPath = androidx.compose.ui.graphics.Path()
        waterPath.moveTo(centerX - 50f, centerY + 70f)
        waterPath.quadraticBezierTo(centerX + 20f, centerY + 50f, centerX + 90f, centerY + 90f)
        waterPath.lineTo(centerX + 110f, centerY + 120f)
        waterPath.quadraticBezierTo(centerX + 40f, centerY + 140f, centerX - 30f, centerY + 110f)
        waterPath.close()
        
        drawPath(
            path = waterPath,
            color = Color(0xFF87CEEB) // iOS water color
        )
        
        // Draw roads (iOS style) - cleaner white roads
        val roadColor = Color.White
        val mainRoadWidth = 3.5.dp.toPx()
        
        // Major roads
        drawLine(
            color = roadColor,
            start = Offset(0f, centerY - 30f),
            end = Offset(size.width, centerY - 30f),
            strokeWidth = mainRoadWidth
        )
        
        drawLine(
            color = roadColor,
            start = Offset(centerX + 30f, 0f),
            end = Offset(centerX + 30f, size.height),
            strokeWidth = mainRoadWidth
        )
        
        // Secondary roads
        val secondaryRoadWidth = 2.dp.toPx()
        drawLine(
            color = roadColor,
            start = Offset(0f, centerY + 70f),
            end = Offset(size.width, centerY + 70f),
            strokeWidth = secondaryRoadWidth
        )
        
        drawLine(
            color = roadColor,
            start = Offset(centerX - 70f, 0f),
            end = Offset(centerX - 70f, size.height),
            strokeWidth = secondaryRoadWidth
        )
        
        // Draw radius circle with iOS blue color
        drawCircle(
            color = Color(0xFF007AFF), // iOS system blue
            radius = radiusPixels,
            center = Offset(centerX, centerY),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Draw filled circle with transparency
        drawCircle(
            color = Color(0xFF007AFF).copy(alpha = 0.15f), // iOS blue with transparency
            radius = radiusPixels,
            center = Offset(centerX, centerY)
        )
        
        // Draw center marker (iOS style)
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
}
